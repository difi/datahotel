package no.difi.datahotel.jersey;

import static no.difi.datahotel.util.jersey.DataFormat.JSON;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import no.difi.datahotel.util.jersey.DataFormat;
import no.difi.datahotel.util.shared.DatahotelException;

public class DatahotelExceptionMapper implements ExceptionMapper<DatahotelException> {

	@Override
	public Response toResponse(DatahotelException e) {
		DataFormat dataFormat = JSON;
		return Response.ok(dataFormat.formatError(e.getMessage(), null)).type(dataFormat.getMime()).status(500).build();
	}

}
