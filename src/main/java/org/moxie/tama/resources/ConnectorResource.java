package org.moxie.tama.resources;

import com.yammer.metrics.annotation.Timed;
import org.moxie.tama.Connector;

import javax.ws.rs.*;

/**
 * User: blangel
 * Date: 5/11/13
 * Time: 5:00 PM
 */
@Path("/connector")
public final class ConnectorResource {

    private final Connector connector;

    public ConnectorResource(Connector connector) {
        this.connector = connector;
    }

    @GET @Timed
    public Integer getScheduledFutures() {
        return connector.getScheduledFutures();
    }

}
