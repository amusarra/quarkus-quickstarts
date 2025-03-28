package org.acme.opentelemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

@Path("/")
public class TracedResource {

    private static final Logger LOG = Logger.getLogger(TracedResource.class);

    @Context
    UriInfo uriInfo;

    @Inject
    OpenTelemetry openTelemetry;

    @Inject
    Meter meter;

    private LongCounter longCounter;

    @PostConstruct
    public void init() {
        meter = openTelemetry.getMeter("myservice");

         longCounter = meter.counterBuilder("service.xvalue")
                .setDescription("Current value of X in the service")
                .setUnit("units")
                .build();
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        LOG.info("hello");
        return "hello";
    }

    @GET
    @Path("/chain")
    @Produces(MediaType.TEXT_PLAIN)
    public String chain() {
        ResourceClient resourceClient = RestClientBuilder.newBuilder()
                .baseUri(uriInfo.getBaseUri())
                .build(ResourceClient.class);
        return "chain -> " + resourceClient.hello();
    }

    @GET
    @Path("/metrics/set")
    @Produces(MediaType.APPLICATION_JSON)
    public String setMetric(@QueryParam("value") long value) {
        if (value <= 0) {
            return "{\"status\":\"failure\", \"message\":\"Value parameter must be positive\"}";
        }
        longCounter.add(value);

        return "{\"status\":\"success\", \"xvalue\":" + value + "}";
    }
}
