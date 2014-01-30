package no.difi.datahotel.resources;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
@Component
@Scope("request")
public class RootResource {

    /**
     * Temporal redirect, the HTML view will be removed.
     */
    @GET
    @Path("/browse")
    public Response getBrowse() {
        return Response.ok().header("location", "/api/html").status(301).build();
    }

    /**
     * Temporal redirect, the HTML view will be removed.
     */
    @GET
    @Path("/def")
    public Response getDefinitions() {
        return Response.ok().header("location", "/api/html/_def").status(301).build();
    }

    /**
     * Content for this URI should be provided by a CMS.
     */
    @GET
    public Response getFrontpage() {
        return Response.ok("Placeholder").build();
    }

    /**
     * Content for this URI should be provided by a CMS.
     */
    @GET
    @Path("/api")
    public Response getApi() {
        return Response.ok("Placeholder").build();
    }
}
