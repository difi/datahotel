package no.difi.datahotel.slave.service;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import no.difi.datahotel.slave.logic.FieldEJB;
import no.difi.datahotel.util.bridge.DefinitionLight;
import no.difi.datahotel.util.jersey.DataFormat;
import no.difi.datahotel.util.jersey.RequestContext;

@Path("/api/{type}/_defs/")
@Stateless
public class DefinitionService extends BaseService {

	Logger logger = Logger.getLogger(DefinitionService.class.getSimpleName());

	@EJB
	private FieldEJB fieldEJB;

	@GET
	@Path("")
	public Response getDefinitions(@PathParam("type") String type, @Context UriInfo uriInfo) {
		DataFormat dataFormat = DataFormat.get(type);
		RequestContext context = new RequestContext(uriInfo);

		try {
			List<DefinitionLight> defs = fieldEJB.getDefinitions();
			Collections.sort(defs);

			if (defs.size() == 0)
				return returnNotFound("No fields available.");

			return Response.ok(dataFormat.format(defs, context)).type(dataFormat.getMime()).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), context)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	@GET
	@Path("{def}")
	public Response getDefinition(@PathParam("type") String type, @PathParam("def") String def, @Context UriInfo uriInfo) {
		DataFormat dataFormat = DataFormat.get(type);
		RequestContext context = new RequestContext(uriInfo);

		try {
			List<String> datasets = fieldEJB.getUsage(def);
			Collections.sort(datasets);

			if (datasets.size() == 0)
				return returnNotFound("Definition never used.");

			return Response.ok(dataFormat.format(datasets, context)).type(dataFormat.getMime()).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), context)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}
}
