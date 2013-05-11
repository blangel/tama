package org.moxie.tama.config;

import com.yammer.dropwizard.config.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * User: blangel
 * Date: 5/11/13
 * Time: 3:17 PM
 */
public final class EmailConfiguration extends Configuration {

    @NotEmpty
    private final String user;

    @NotEmpty
    private final String encryptedPassword;

    @NotEmpty
    private final String smtpHost;

    @NotEmpty
    private final String smtpPort;

    private EmailConfiguration() {
        this(null, null, null, null);
    }

    public EmailConfiguration(String user, String encryptedPassword, String smtpHost, String smtpPort) {
        this.user = user;
        this.encryptedPassword = encryptedPassword;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    public String getUser() {
        return user;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }
}
