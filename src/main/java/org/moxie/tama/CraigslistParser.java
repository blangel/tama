package org.moxie.tama;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 10:39 AM
 */
public final class CraigslistParser {

    private static final Logger LOG = LoggerFactory.getLogger(CraigslistParser.class);

    private static final Pattern DATE1_REGEX = Pattern.compile(".*class=\"date\">(...  \\d)</span>.*");
    private static final Pattern DATE2_REGEX = Pattern.compile(".*class=\"date\">(... \\d\\d)</span>.*");
    private static final Pattern LINK_REGEX = Pattern.compile(".*(<a href=\".*html\">.*?</a>).*");
    private static final Pattern LINK_TEXT_REGEX = Pattern.compile(".*?>(.*)</a>");
    private static final Pattern PRICE_REGEX = Pattern.compile(".*<span class=\"price\">\\$(\\d*)</span>.*");
    private static final Pattern BR_REGEX = Pattern.compile(".* / (\\d)br.*");
    private static final Pattern LOCATION_REGEX = Pattern.compile(".*<span class=\"pp\"></span> <small> \\((.*?)\\)</small>.*");

    public static final class Result {
        final String date;
        final String link;
        final String price;
        final String br;
        final String location;
        Result(String date, String link, String price, String br, String location) {
            this.date = date;
            this.link = link;
            this.price = price;
            this.br = br;
            this.location = location;
        }
    }

    public static List<String> parse(List<String> rawResults, String baseUrl, Rule ... rules) {
        if ((rawResults == null) || rawResults.isEmpty()) {
            return null;
        }
        if (rules == null) {
            rules = new Rule[0];
        }
        Set<String> results = new LinkedHashSet<String>();
        Set<String> existingLinks = new HashSet<String>();
        Date today = new Date();
        for (String rawResult : rawResults) {
            Parser parser = new Parser();
            try {
                parser.setInputHTML(rawResult);
                NodeList nodeList = parser.parse(new AndFilter(new TagNameFilter("p"),
                                                               new HasParentFilter(new TagNameFilter("blockquote"))));
                SimpleNodeIterator simpleNodeIterator =nodeList.elements();
                nodes:while (simpleNodeIterator.hasMoreNodes()) {
                    ParagraphTag node = (ParagraphTag) simpleNodeIterator.nextNode();
                    String nodeInnerHtml = node.getStringText();
                    for (Rule rule : rules) {
                        if (!rule.accept(nodeInnerHtml)) {
                            continue nodes;
                        }
                    }
                    Result result = extractResult(baseUrl, nodeInnerHtml);
                    result = filterByDate(result, today);
                    String linkText = (result == null ? null : extractLinkText(result.link));
                    if ((result != null) && existingLinks.add(linkText)) {
                        String htmlResult = String.format("<p><span>%s&nbsp;&nbsp;</span>%s<span>&nbsp;$%s&nbsp;</span><span>&nbsp;%sbr&nbsp;</span><span>&nbsp;%s&nbsp;</span></p>",
                                result.date, result.link, result.price, result.br, result.location);
                        results.add(htmlResult);
                    }
                }
            } catch (ParserException pe) {
                // ignore
            }
        }

        return new ArrayList<String>(results);
    }

    static Result filterByDate(Result result, Date from) {
        if (result == null) {
            return null;
        }
        String date = result.date;
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        try {
            Date parsed = formatter.parse(date);
            Calendar parsedCal = Calendar.getInstance();
            parsedCal.setTime(parsed);
            Calendar cal = Calendar.getInstance();
            cal.setTime(from);
            if ((cal.get(Calendar.MONTH) == Calendar.JANUARY) && (parsedCal.get(Calendar.MONTH) == Calendar.DECEMBER)) {
                parsedCal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
            } else {
                parsedCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            }
            long start = cal.getTimeInMillis();
            long then = parsedCal.getTimeInMillis();
            if ((start - then) > (1000L * 60L * 60L * 24L * 7L)) {
                return null; // filter out earlier than last week
            } else {
                return result;
            }
        } catch (ParseException pe) {
            return result;
        }
    }

    static Result extractResult(String baseUrl, String text) {
        text = text.replace('\n', '\0');
        String date = extractDate(text).trim();
        String link = extractLink(text).trim();
        link = link.replace("<a href=\"", String.format("<a href=\"%s", baseUrl));
        String price = extractPrice(text).trim();
        String br = extractBedroom(text).trim();
        String location = extractLocation(text).trim();
        if (date.isEmpty() || link.isEmpty() || price.isEmpty() || br.isEmpty()) {
            LOG.error("Could not extract info [^cyan^{}^r^, ^green^{}^r^, ^yellow^{}^r^, ^red^{}^r^, ^black^{}^r^]: {}", date, link, price, br, location, text);
            return null;
        } else {
            return new Result(date, link, price, br, location);
        }
    }

    private static String extractDate(String text) {
        Matcher matcher = DATE1_REGEX.matcher(text);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        matcher = DATE2_REGEX.matcher(text);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }

    private static String extractLink(String text) {
        Matcher matcher = LINK_REGEX.matcher(text);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }

    private static String extractLinkText(String link) {
        Matcher matcher = LINK_TEXT_REGEX.matcher(link);
        if (matcher.matches()) {
            return matcher.group(1).trim();
        }
        return link;
    }

    private static String extractPrice(String text) {
        Matcher matcher = PRICE_REGEX.matcher(text);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }

    private static String extractBedroom(String text) {
        Matcher matcher = BR_REGEX.matcher(text);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }

    private static String extractLocation(String text) {
        Matcher matcher = LOCATION_REGEX.matcher(text);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }
    
    private CraigslistParser() { }

}
