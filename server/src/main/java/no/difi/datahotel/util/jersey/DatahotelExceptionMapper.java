package no.difi.datahotel.util.jersey;

import static no.difi.datahotel.util.Formater.JSON;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import no.difi.datahotel.util.DatahotelException;
import no.difi.datahotel.util.Formater;

public class DatahotelExceptionMapper implements ExceptionMapper<DatahotelException> {

	@Override
	public Response toResponse(DatahotelException e) {
		Formater dataFormat = JSON;
		return Response.ok(dataFormat.formatError(e.getMessage(), null)).type(dataFormat.getMime()).status(500).build();
	}

}
