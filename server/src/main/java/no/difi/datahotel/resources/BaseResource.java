package no.difi.datahotel.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.DatahotelException;

public abstract class BaseResource {

	@Context
	protected UriInfo uriInfo;
	@Context
	protected Request request;
	
	protected void checkNotModified(Metadata metadata) {
		/* EntityTag eTag = new EntityTag(String.valueOf(metadata.getUpdated()));
		if (request.evaluatePreconditions(eTag) != null)
			throw new DatahotelException(304, "Not modified"); */
	}
	
	protected Response returnNotFound(String message) throws Exception {
		throw new DatahotelException(404, message);
	}
}
