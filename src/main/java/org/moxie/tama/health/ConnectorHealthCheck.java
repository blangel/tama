package org.moxie.tama.health;

import com.yammer.metrics.core.HealthCheck;
import org.moxie.tama.Connector;

/**
 * User: blangel
 * Date: 5/11/13
 * Time: 4:57 PM
 */
public final class ConnectorHealthCheck extends HealthCheck {

    private final Connector connector;

    public ConnectorHealthCheck(Connector connector) {
        super("connector");
        this.connector = connector;
    }

    @Override protected Result check() throws Exception {
        return (connector.getScheduledFutures() > 0 ? Result.healthy() : Result.unhealthy("No scheduled futures."));
    }
}
