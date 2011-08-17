package org.moxie.tama;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * User: blangel
 * Date: 6/25/11
 * Time: 2:20 PM
 */
public class ConnectorTest {

    @Test
    public void testPropertiesFileParsing() {
        List<Profile> profiles = Connector.readProfilesFromPropertyFiles(new File("./src/test/resources"));
        assertEquals(2, profiles.size());
        
        Profile brianProfile = profiles.get(0);
        assertEquals("brian", brianProfile.getName());
        assertEquals("langelb@gmail.com", brianProfile.getEmailAddress());
        assertEquals(1, brianProfile.getUpdateFrequencyInHours());
        assertEquals(1, brianProfile.sendEmailFrequencyInHours);
        assertEquals(6, brianProfile.getQueries().length);
        assertEquals(15, brianProfile.getRules().length);
        assertFalse(brianProfile.getRemove());

        Profile megsieProfile = profiles.get(1);
        assertEquals("megsie", megsieProfile.getName());
        assertEquals("blangel.hbo@gmail.com", megsieProfile.getEmailAddress());
        assertEquals(1, megsieProfile.getUpdateFrequencyInHours());
        assertEquals(2, megsieProfile.sendEmailFrequencyInHours);
        assertEquals(21, megsieProfile.getQueries().length);
        assertEquals(12, megsieProfile.getRules().length);
        assertFalse(megsieProfile.getRemove());
    }

}
