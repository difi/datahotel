package no.difi.datahotel.resources;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import no.difi.datahotel.util.DatahotelException;

@Provider
@Component
public class DatahotelExceptionMapper implements ExceptionMapper<DatahotelException> {

	@Override
	public Response toResponse(DatahotelException e) {
		switch (e.getStatus()) {

		case 304:
			return Response.ok().status(304).build();

		default:
			return Response.ok(e.getFormater().formatError(e, null)).type(e.getFormater().getMime())
					.status(e.getStatus()).build();

		}
	}
}