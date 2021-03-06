package org.moxie.tama;

import com.yammer.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 10:38 AM
 */
public class Connector implements Runnable, Managed {

    private static final Logger LOG = LoggerFactory.getLogger(Connector.class);

    protected final ScheduledExecutorService executor;

    protected final ConcurrentMap<String, Profile> profiles;

    protected final ConcurrentMap<String, ScheduledFuture> futures;

    protected final EmailService emailService;

    private final AtomicReference<ScheduledFuture<?>> task;

    public Connector(ScheduledExecutorService executor, EmailService emailService) {
        this.executor = executor;
        this.profiles = new ConcurrentHashMap<String, Profile>();
        this.futures = new ConcurrentHashMap<String, ScheduledFuture>();
        this.emailService = emailService;
        this.task = new AtomicReference<ScheduledFuture<?>>();
    }

    @Override public void start() {
        ScheduledFuture<?> task = executor.scheduleAtFixedRate(this, 0L, 30L, TimeUnit.MINUTES);
        this.task.set(task);
    }

    @Override public void stop() throws Exception {
        ScheduledFuture<?> task = this.task.get();
        if (task != null) {
            task.cancel(true);
        }
        for (ScheduledFuture future : this.futures.values()) {
            future.cancel(true);
        }
    }

    public int getScheduledFutures() {
        return futures.size();
    }

    @Override public void run() {
        List<Profile> profiles = readProfilesFromPropertyFiles(new File("."));
        for (final Profile profile : profiles) {
            if (profile == null) {
                continue;
            }
            if (this.profiles.containsKey(profile.getName())) {
                Profile existing = this.profiles.get(profile.getName());
                if (profile.equals(existing) && !profile.getRemove()) {
                    continue;
                } else {
                    profile.getPreviousResults().addAll(existing.getPreviousResults());
                }
            }
            // cancel existing if any.
            ScheduledFuture scheduledFuture = futures.remove(profile.getName());
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
            if (profile.getRemove()) {
                continue;
            }
            // add new.
            this.profiles.put(profile.getName(), profile);
            futures.put(profile.getName(), executor.scheduleAtFixedRate(new Runnable() {
                        @Override public void run() {
                            List<String> rawResults = CraigslistQuerier.query(profile.getQueries());
                            List<String> results = CraigslistParser.parse(rawResults, profile.getBaseUrl(), profile.getRules());
                            boolean shouldEmail = profile.shouldEmail();
                            if ((results != null) && !results.isEmpty()) {
                                Collections.sort(results, profile.getSort());
                                results.removeAll(profile.getPreviousResults());
                                results.removeAll(profile.getCollectingResults());
                                profile.getCollectingResults().addAll(results);
                                if (shouldEmail) {
                                    emailService.email(profile.getCollectingResults(), profile.getEmailAddresses());
                                    profile.getPreviousResults().addAll(profile.getCollectingResults());
                                    profile.getCollectingResults().clear();
                                    // only keep the last 50 results
                                    if (profile.getPreviousResults().size() > 50) {
                                        int numToRemove = 50 - profile.getPreviousResults().size();
                                        for (int i = 0; i < numToRemove; i++) {
                                            profile.getPreviousResults().remove(0);
                                        }
                                    }
                                }
                            } else {
                                int updateFrequency = profile.getUpdateFrequencyInHours();
                                LOG.info("No results for {}, will retry in {} hour{}", profile.getName(), updateFrequency, (updateFrequency > 1 ? "s." : "."));
                            }
                        }
                    }, 0L, profile.getUpdateFrequencyInHours(), TimeUnit.HOURS));
        }

    }

    protected static List<Profile> readProfilesFromPropertyFiles(File currentDirectory) {
        File[] propertyFiles = currentDirectory.listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
                return ((name != null) && name.endsWith(".properties"));
            }
        });
        if (propertyFiles == null) {
            return Collections.emptyList();
        }
        List<Profile> profiles = new ArrayList<Profile>(propertyFiles.length);
        for (File file : propertyFiles) {
            Properties properties;
            Reader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                properties = new Properties();
                properties.load(reader);
            } catch (IOException ioe) {
                continue;
            }
            String name = properties.getProperty("name");
            String emailAddress = properties.getProperty("email");
            String baseUrl = properties.getProperty("baseUrl");
            Integer update, sendEmail; Boolean remove;
            try {
                update = Integer.parseInt(properties.getProperty("update"));
                String sendEmailAsString = properties.getProperty("sendEmail");
                if (sendEmailAsString != null) {
                    sendEmail = Integer.parseInt(sendEmailAsString);
                } else {
                    sendEmail = update;
                }
                remove = Boolean.parseBoolean(properties.getProperty("remove"));
                String queriesString = properties.getProperty("queries");
                String rulesString = properties.getProperty("rules");
                List<Query> queries = parseQueries(queriesString);
                List<Rule> rules = parseRules(rulesString);
                LOG.info("Found {} queries and {} rules for {} | {}", queries.size(), rules.size(), name, emailAddress);
                profiles.add(new Profile(name, emailAddress, update, sendEmail, baseUrl,
                        queries.toArray(new Query[queries.size()]),
                        rules.toArray(new Rule[rules.size()]), new Sort.MoneyAsc(), remove));
            } catch (RuntimeException re) {
                LOG.error(re.getMessage(), re);
            }
        }
        return profiles;
    }

    protected static List<Query> parseQueries(String queriesString) {
        if (queriesString == null) {
            return Collections.emptyList();
        }
        String[] separated = queriesString.split("(?<!\\\\),");
        List<Query> queries = new ArrayList<Query>(separated.length);
        for (String query : separated) {
            try {
                queries.add(new Query.Default(query));
            } catch (RuntimeException re) {
                LOG.error(re.getMessage(), re);
            }
        }
        return queries;
    }

    protected static List<Rule> parseRules(String rulesString) {
        if (rulesString == null) {
            return Collections.emptyList();
        }
        String[] separated = rulesString.split("(?<!\\\\),");
        List<Rule> rules = new ArrayList<Rule>(separated.length);
        for (String rulePart : separated) {
            Rule rule = parseRule(rulePart);
            if (rule != null) {
                rules.add(rule);
            }
        }
        return rules;
    }

    protected static Rule parseRule(String rulePart) {
        if (rulePart == null) {
            return null;
        }
        String[] separated = rulePart.split("(?<!\\\\):");
        if (separated.length < 2) {
            return null;
        }
        if ("default".equals(separated[0])) {
            try {
                return new Rule.Default(separated[1], Boolean.parseBoolean(separated[2]));
            } catch (RuntimeException re) {
                LOG.error(re.getMessage(), re);
                return null;
            }
        } else if ("maxmoney".equals(separated[0])) {
            try {
                return new Rule.MaxMoney(Integer.parseInt(separated[1]));
            } catch (RuntimeException re) {
                LOG.error(re.getMessage(), re);
                return null;
            }
        } else {
            LOG.error("Unknown Rule type, skipping.");
            return null;
        }
    }

}