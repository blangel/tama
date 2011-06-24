package org.moxie.tama;

import java.util.ArrayList;
import java.util.List;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 10:59 AM
 */
public final class CraigslistQuerier {

    public static List<String> query(Query ... queries) {
        if (queries == null) {
            return null;
        }
        List<String> rawResults = new ArrayList<String>(queries.length);
        for (Query query : queries) {
            String rawResult = query.query();
            if (rawResult != null) {
                rawResults.add(rawResult);
            }
        }
        return rawResults;
    }
    
    private CraigslistQuerier() { }

}
