package no.difi.datahotel.util.jersey;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import no.difi.datahotel.util.DatahotelException;

@Provider
public class DatahotelExceptionMapper implements ExceptionMapper<DatahotelException> {

	@Override
	public Response toResponse(DatahotelException e) {
		return Response.ok(e.getFormater().formatError(e, null)).type(e.getFormater().getMime()).status(e.getStatus()).build();
	}

}
