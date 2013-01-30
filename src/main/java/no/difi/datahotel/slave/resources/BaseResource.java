package no.difi.datahotel.slave.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.DatahotelException;

import com.sun.jersey.api.core.HttpRequestContext;

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
