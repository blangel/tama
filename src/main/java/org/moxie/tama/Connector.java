package org.moxie.tama;

import com.sun.tools.javac.code.Source;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 10:38 AM
 */
public class Connector {

    public static void main(String[] args) {

        Connector connector = new Connector();
        connector.start();

    }

    protected final EmailService emailService;

    protected final Query[] queries;

    protected final Rule[] rules;

    protected final Sort sort;

    private Connector() {
        emailService = new EmailService();
        queries = new Query[]
                {
                new Query.Default("http://newyork.craigslist.org/search/nfa/brk?query=boerum+hill&srchType=A&minAsk=&maxAsk=&bedrooms=1&hasPic=1"),
                new Query.Default("http://newyork.craigslist.org/search/nfa/brk?query=park+slope&srchType=A&minAsk=&maxAsk=&bedrooms=1&hasPic=1"),
                new Query.Default("http://newyork.craigslist.org/search/nfa/brk?query=fort+greene&srchType=A&minAsk=&maxAsk=&bedrooms=1&hasPic=1"),
                new Query.Default("http://newyork.craigslist.org/search/nfa/brk?query=prospect+heights&srchType=A&minAsk=&maxAsk=&bedrooms=1&hasPic=1"),
                new Query.Default("http://newyork.craigslist.org/search/nfa/brk?query=brooklyn+heights&srchType=A&minAsk=&maxAsk=&bedrooms=1&hasPic=1"),
                new Query.Default("http://newyork.craigslist.org/search/nfa/brk?query=green+point&srchType=A&minAsk=&maxAsk=&bedrooms=1&hasPic=1")
                };
        rules = new Rule[]
                {
                new Rule.Default("1br", true),
                new Rule.Default("CROWN HEIGHTS", false),
                new Rule.Default("Crown Heights", false),
                new Rule.Default("WILLIAMSBURG", false),
                new Rule.Default("Willaimsburg", false),
                new Rule.Default("BEDFORD STUYESANT", false),
                new Rule.Default("BED STY", false),
                new Rule.Default("Bedford Stuyvesant", false),
                new Rule.Default("BUSHWICK", false),
                new Rule.Default("Bushwick", false),
                new Rule.Default("Crwon Heights", false),
                new Rule.Default("Stuyvesant Heights", false),
                new Rule.MaxMoney(1600)
                };
        sort = new Sort.MoneyAsc();
    }

    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(
            new Runnable() {
                @Override public void run() {
                    List<String> rawResults = CraigslistQuerier.query(queries);
                    List<String> results = CraigslistParser.parse(rawResults, rules);
                    if ((results != null) && !results.isEmpty()) {
                        Collections.sort(results, sort);
                        emailService.email(results);
                    } else {
                        System.out.println("No results, will retry in one hour.");
                    }
                }
            }, 0L, 1L, TimeUnit.HOURS);

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }

}