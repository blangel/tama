package org.moxie.tama;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import org.jasypt.util.text.BasicTextEncryptor;
import org.moxie.tama.config.EmailConfiguration;
import org.moxie.tama.config.TamaConfiguration;
import org.moxie.tama.health.ConnectorHealthCheck;
import org.moxie.tama.resources.ConnectorResource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * User: blangel
 * Date: 5/11/13
 * Time: 3:33 PM
 */
public final class TamaService extends Service<TamaConfiguration> {

    public static void main(String[] args) throws Exception {
        new TamaService().run(args);
    }

    protected final ConcurrentMap<String, Profile> profiles;

    public TamaService() {
        this.profiles = new ConcurrentHashMap<String, Profile>();
    }

    @Override public void initialize(Bootstrap<TamaConfiguration> bootstrap) { }

    @Override public void run(TamaConfiguration configuration, Environment environment) throws Exception {
        EmailConfiguration emailConfiguration = configuration.getEmailConfiguration();

        String pwd = decrypt(configuration.getEncryptionPassphrase(), emailConfiguration.getEncryptedPassword());
        final EmailService emailService = new EmailService(emailConfiguration.getUser(), pwd, emailConfiguration.getSmtpHost(), emailConfiguration.getSmtpPort());

        ScheduledExecutorService scheduledExecutorService = environment.managedScheduledExecutorService("Task-%d", 1);

        Connector connector = new Connector(scheduledExecutorService, emailService);
        environment.manage(connector);

        environment.addHealthCheck(new ConnectorHealthCheck(connector));
        environment.addResource(new ConnectorResource(connector));
    }

    private String decrypt(String password, String encrypted) {
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(password);
        return encryptor.decrypt(encrypted);
    }
}
