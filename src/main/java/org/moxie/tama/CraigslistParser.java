package org.moxie.tama;

import com.sun.org.apache.xpath.internal.NodeSet;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 10:39 AM
 */
public final class CraigslistParser {

    public static List<String> parse(List<String> rawResults, Rule ... rules) {
        if ((rawResults == null) || rawResults.isEmpty()) {
            return null;
        }
        if (rules == null) {
            rules = new Rule[0];
        }
        List<String> results = new ArrayList<String>();
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
                    results.add("<p>" + nodeInnerHtml + "</p>");
                }
            } catch (ParserException pe) {
                // ignore
            }
        }

        return results;
    }
    
    private CraigslistParser() { }

}
