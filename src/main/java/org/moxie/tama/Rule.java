package org.moxie.tama;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 10:38 AM
 */
public interface Rule {

    static class Default implements Rule {

        protected final Pattern regexPattern;

        protected final boolean positiveMatching;

        public Default(String regex, boolean positiveMatching) {
            regexPattern = Pattern.compile(regex);
            this.positiveMatching = positiveMatching;
        }

        @Override public boolean accept(String text) {
            Matcher matcher = regexPattern.matcher(text);
            return (matcher.find() == positiveMatching);
        }
    }

    static class MaxMoney implements Rule {

        public static final String CRAIGSLIST_PRICE_REGEX = "(\\$\\d\\d\\d\\d)";

        protected final int maximum;

        protected final Pattern pricePattern;

        public MaxMoney(int maximum) {
            pricePattern = Pattern.compile(CRAIGSLIST_PRICE_REGEX);
            this.maximum = maximum;
        }

        @Override public boolean accept(String text) {
            Matcher matcher = pricePattern.matcher(text);
            if (!matcher.find()) {
                return true; // i guess?
            }
            String match = matcher.group();
            if ((match == null) || match.isEmpty()) {
                return true; // again, i guess?
            }
            try {
                Integer amount = Integer.parseInt(match.substring(1));
                return (amount == null) || (amount <= maximum);
            } catch (NumberFormatException nfe) {
                return true; // yep again, i guess?
            }
        }
    }

    boolean accept(String text);

}
