package org.moxie.tama;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 1:03 PM
 */
public interface Sort extends Comparator<String> {
    
    static class MoneyAsc implements Sort {

        protected final Pattern pricePattern;

        public MoneyAsc() {
            pricePattern = Pattern.compile(Rule.MaxMoney.CRAIGSLIST_PRICE_REGEX);
        }

        @Override public int compare(String o1, String o2) {
            Matcher matcher1 = pricePattern.matcher(o1);
            String match1 = "";
            if (matcher1.find()) {
                match1 = matcher1.group();
            }
            Matcher matcher2 = pricePattern.matcher(o2);
            String match2 = "";
            if (matcher2.find()) {
                match2 = matcher2.group();
            }
            return match1.compareTo(match2);
        }
    }

}
