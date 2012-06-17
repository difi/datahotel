package no.difi.datahotel.slave.service;

import java.util.ArrayList;
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

import no.difi.datahotel.model.DefinitionLight;
import no.difi.datahotel.model.Field;
import no.difi.datahotel.slave.logic.FieldEJB;
import no.difi.datahotel.util.DatahotelException;
import no.difi.datahotel.util.Formater;
import no.difi.datahotel.util.RequestContext;

@Path("/api/{type}/_def/")
@Stateless
public class DefinitionService {

	Logger logger = Logger.getLogger(DefinitionService.class.getSimpleName());

	@EJB
	private FieldEJB fieldEJB;

	@GET
	public Response getDefinitions(@PathParam("type") String type, @Context UriInfo uriInfo) {
		Formater dataFormat = Formater.get(type);
		RequestContext context = new RequestContext(uriInfo);

		try {
			List<DefinitionLight> defs = fieldEJB.getDefinitions();
			Collections.sort(defs);

			if (defs.size() == 0)
				return returnNotFound("No fields available.");

			return Response.ok(dataFormat.format(defs, context)).type(dataFormat.getMime()).build();
		} catch (DatahotelException e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), context)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	@GET
	@Path("{def}")
	public Response getDefinition(@PathParam("type") String type, @PathParam("def") String def, @Context UriInfo uriInfo) {
		Formater dataFormat = Formater.get(type);
		RequestContext context = new RequestContext(uriInfo);

		try {
			List<String> datasets = new ArrayList<String>();
			for (Field f : fieldEJB.getUsage(def))
				if (!datasets.contains(f.getMetadata().getLocation()))
					datasets.add(f.getMetadata().getLocation());
			Collections.sort(datasets);

			if (datasets.size() == 0)
				return returnNotFound("Definition never used.");

			return Response.ok(dataFormat.format(datasets, context)).type(dataFormat.getMime()).build();
		} catch (DatahotelException e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), context)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	/**
	 * TODO Remove when fixed.
	 */
	protected Response returnNotFound(String message) throws Exception {
		throw new DatahotelException(message);
		// return Response.ok().status(404).build();
	}
}
