package no.difi.datahotel.slave.service;

import java.io.File;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/")
public class RootService {

	@GET
	@Path("/style.css")
	public Response getStylesheet(@Context ServletContext context) {
		return Response.ok(new File(context.getRealPath("/style.css")), "text/css").build();
	}

	@GET
	@Path("/favicon.ico")
	public Response getFavicon(@Context ServletContext context) {
		return Response.ok(new File(context.getRealPath("/favicon.ico")), "image/x-icon").build();
	}

	@GET
	@Path("/browse")
	public Response getBrowse() {
		return Response.ok().header("location", "/api/html").status(301).build();
	}

	@GET
	public Response getFrontpage() {
		return Response.ok("Placeholder").build();
	}

	@GET
	@Path("/api")
	public Response getApi() {
		return Response.ok("Placeholder").build();
	}
}
