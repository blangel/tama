package org.moxie.tama;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: blangel
 * Date: 6/25/11
 * Time: 2:20 PM
 */
public class ConnectorTest {

    @Test
    public void testPropertiesFileParsing() {
        List<Profile> profiles = Connector.readProfilesFromPropertyFiles(new File("/home/blangel/projects/tama/src/test/resources"));
        assertEquals(1, profiles.size());
        Profile brianProfile = profiles.get(0);
        assertEquals("brian", brianProfile.getName());
        assertEquals("langelb@gmail.com", brianProfile.getEmailAddress());
        assertEquals(1, brianProfile.getUpdateFrequencyInHours());
        assertEquals(6, brianProfile.getQueries().length);
        assertEquals(15, brianProfile.getRules().length);
    }

}
