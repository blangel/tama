package org.moxie.tama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 4:06 PM
 */
public class Profile {

    protected final String name;

    protected final String emailAddresses;

    protected final int updateFrequencyInHours;

    protected final int sendEmailFrequencyInHours;

    protected final String baseUrl;

    protected final AtomicInteger rollingSendEmailChecker;

    protected final Query[] queries;

    protected final Rule[] rules;

    protected final Sort sort;

    protected final List<String> previousResults;

    protected final List<String> collectingResults;

    protected final Boolean remove;

    public Profile(String name, String emailAddresses, int updateFrequencyInHours, int sendEmailFrequencyInHours,
                   String baseUrl, Query[] queries, Rule[] rules, Sort sort, Boolean remove) {
        this.name = name;
        this.emailAddresses = emailAddresses;
        this.updateFrequencyInHours = updateFrequencyInHours;
        this.sendEmailFrequencyInHours = sendEmailFrequencyInHours;
        this.rollingSendEmailChecker = new AtomicInteger(0);
        this.baseUrl = baseUrl;
        this.queries = queries;
        this.rules = rules;
        this.sort = sort;
        this.previousResults = new ArrayList<String>();
        this.collectingResults = new ArrayList<String>();
        this.remove = remove;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddresses() {
        return emailAddresses;
    }

    public int getUpdateFrequencyInHours() {
        return updateFrequencyInHours;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Query[] getQueries() {
        return queries;
    }

    public Rule[] getRules() {
        return rules;
    }

    public Sort getSort() {
        return sort;
    }

    public List<String> getPreviousResults() {
        return previousResults;
    }

    public List<String> getCollectingResults() {
        return collectingResults;
    }

    public boolean shouldEmail() {
        return ((rollingSendEmailChecker.getAndAdd(1) % sendEmailFrequencyInHours) == 0);
    }

    public Boolean getRemove() {
        return remove;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        Profile that = (Profile) o;
        if ((emailAddresses == null) ? (that.emailAddresses != null) : !emailAddresses.equals(that.emailAddresses)) {
            return false;
        }
        if ((name == null) ? (that.name != null) : !name.equals(that.name)) {
            return false;
        }
        if (updateFrequencyInHours != that.updateFrequencyInHours) {
            return false;
        }
        return Arrays.equals(queries, that.queries) && Arrays.equals(rules, that.rules);
    }

    @Override public int hashCode() {
        int result = (name == null ? 0 : name.hashCode());
        result = 31 * result + (emailAddresses == null ? 0 : emailAddresses.hashCode());
        result = 31 * result + updateFrequencyInHours;
        result = 31 * result + (queries == null ? 0 : Arrays.hashCode(queries));
        result = 31 * result + (rules == null ? 0 : Arrays.hashCode(rules));
        return result;
    }
}
