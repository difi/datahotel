package no.difi.datahotel.slave.service;

import javax.ws.rs.core.Response;

import no.difi.datahotel.util.DatahotelException;

public abstract class BaseService {
	protected Response returnNotModified() {
		return Response.ok().status(304).build();
	}
	
	protected Response returnNotFound(String message) throws Exception {
		throw new DatahotelException(message);
		// return Response.ok().status(404).build();
	}
}
