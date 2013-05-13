package org.moxie.tama;

import org.junit.Test;

import java.util.Calendar;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * User: blangel
 * Date: 5/12/13
 * Time: 4:10 PM
 */
public final class CraigslistParserTest {

    private static final String item = "<a href=\"/brk/fee/3765660867.html\" class=\"i\"></a> <span class=\"pl\"> <span class=\"star\"></span> <small><span class=\"date\">Apr 25</span></small> <a href=\"/brk/fee/3765660867.html\">Incredible 2BR on the Columbia Street Water Front border!</a> </span> <span class=\"l2\"> <span class=\"pnr\"> <span class=\"price\">$2800</span> / 2br -  <span class=\"pp\"></span> <small> (\n" +
            "    Carroll Gardens\n" +
            "    \t)</small> <span class=\"px\"> <span class=\"p\"> img</span></span> </span>  <a class=\"gc\" href=\"/fee/\" data-cat=\"fee\">apts broker fee</a> </span> \n";

    @Test
    public void extractResult() {
        assertNotNull(CraigslistParser.extractResult(item));
    }

    @Test
    public void filterByDate() {
        CraigslistParser.Result result = new CraigslistParser.Result("Apr 26", "nil", "nil", "nil", "nil");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.APRIL);
        cal.set(Calendar.DAY_OF_MONTH, 20);
        result = CraigslistParser.filterByDate(result, cal.getTime());
        assertNotNull(result);

        cal.set(Calendar.MONTH, Calendar.MAY);
        cal.set(Calendar.DAY_OF_MONTH, 5);
        result = CraigslistParser.filterByDate(result, cal.getTime());
        assertNull(result);

        result = new CraigslistParser.Result("Dec 31", "nil", "nil", "nil", "nil");
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        result = CraigslistParser.filterByDate(result, cal.getTime());
        assertNotNull(result);

        cal.set(Calendar.DAY_OF_MONTH, 8);
        result = CraigslistParser.filterByDate(result, cal.getTime());
        assertNull(result);
    }

}
