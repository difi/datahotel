package no.difi.datahotel.client.slave;

import javax.ws.rs.core.Response;

public abstract class BaseService {
	protected Response returnNotModified() {
		return Response.ok().status(304).build();
	}
	
	protected Response returnNotFound(String message) throws Exception {
		throw new Exception(message);
		// return Response.ok().status(404).build();
	}
}
