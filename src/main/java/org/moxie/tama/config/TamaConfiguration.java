package org.moxie.tama.config;

import com.yammer.dropwizard.config.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * User: blangel
 * Date: 5/11/13
 * Time: 3:12 PM
 */
public final class TamaConfiguration extends Configuration {

    @Valid
    @NotNull
    private final EmailConfiguration emailConfiguration;

    @Valid
    @NotNull
    private final String encryptionPassphrase;

    private TamaConfiguration() {
        this(null, null);
    }

    public TamaConfiguration(EmailConfiguration emailConfiguration, String encryptionPassphrase) {
        this.emailConfiguration = emailConfiguration;
        this.encryptionPassphrase = encryptionPassphrase;
    }

    public EmailConfiguration getEmailConfiguration() {
        return emailConfiguration;
    }

    public String getEncryptionPassphrase() {
        return encryptionPassphrase;
    }
}
