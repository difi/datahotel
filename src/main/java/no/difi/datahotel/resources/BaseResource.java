package no.difi.datahotel.resources;

import com.sun.jersey.api.core.HttpRequestContext;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.DatahotelException;

import javax.ws.rs.core.*;

public abstract class BaseResource {

    @Context
    protected UriInfo uriInfo;
    @Context
    protected Request request;

    protected void checkNotModified(Metadata metadata) {
        EntityTag eTagCurrent = new EntityTag(String.valueOf(metadata.getUpdated()));

        HttpRequestContext context = (HttpRequestContext) request;
        if (context.getHeaderValue(HttpHeaders.IF_NONE_MATCH) == null)
            return;

        EntityTag eTagRequest = new EntityTag(context.getHeaderValue(HttpHeaders.IF_NONE_MATCH));

        // if (request.evaluatePreconditions(eTag) != null)
        if (eTagCurrent.equals(eTagRequest))
            throw new DatahotelException(304, "Not modified");
    }

    protected Response returnNotFound(String message) throws Exception {
        throw new DatahotelException(404, message);
    }
}
