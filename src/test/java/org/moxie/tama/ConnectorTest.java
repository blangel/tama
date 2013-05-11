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
        assertEquals("blangel.hbo@gmail.com", brianProfile.getEmailAddress());
        assertEquals(1, brianProfile.getUpdateFrequencyInHours());
        assertEquals(8, brianProfile.sendEmailFrequencyInHours);
        assertEquals(4, brianProfile.getQueries().length);
        assertEquals(5, brianProfile.getRules().length);
        assertFalse(brianProfile.getRemove());

        Profile megsieProfile = profiles.get(1);
        assertEquals("megsie", megsieProfile.getName());
        assertEquals("blangel.hbo@gmail.com", megsieProfile.getEmailAddress());
        assertEquals(1, megsieProfile.getUpdateFrequencyInHours());
        assertEquals(8, megsieProfile.sendEmailFrequencyInHours);
        assertEquals(4, megsieProfile.getQueries().length);
        assertEquals(5, megsieProfile.getRules().length);
        assertFalse(megsieProfile.getRemove());
    }

}
