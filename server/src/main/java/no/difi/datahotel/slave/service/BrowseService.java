package no.difi.datahotel.slave.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.MetadataLight;
import no.difi.datahotel.model.Result;
import no.difi.datahotel.slave.logic.ChunkEJB;
import no.difi.datahotel.slave.logic.DataEJB;
import no.difi.datahotel.slave.logic.FieldEJB;
import no.difi.datahotel.slave.logic.SearchEJB;
import no.difi.datahotel.util.DatahotelException;
import no.difi.datahotel.util.Formater;
import no.difi.datahotel.util.RequestContext;

@Path("/api/{type}/")
@Stateless
public class BrowseService extends BaseService {

	Logger logger = Logger.getLogger(BrowseService.class.getSimpleName());

	@EJB
	private DataEJB dataEJB;
	@EJB
	private FieldEJB fieldEJB;
	@EJB
	private ChunkEJB chunkEJB;
	@EJB
	private SearchEJB searchEJB;

	@GET
	public Response getOwnerList(@PathParam("type") String type, @Context HttpServletRequest req,
			@Context UriInfo uriInfo) {
		return getDataset(type, "", req, uriInfo);
	}

	@GET
	@Path("_all")
	public Response getAllDatasets(@PathParam("type") String type, @Context UriInfo uriInfo) {
		Formater dataFormat = Formater.get(type);
		RequestContext context = new RequestContext(uriInfo);

		try {
			List<MetadataLight> list = new ArrayList<MetadataLight>();
			for (Metadata m : dataEJB.getDatasets())
				list.add(m.light());

			if (list.size() == 0)
				return returnNotFound("No elements found.");

			return Response.ok(dataFormat.format(list, context)).type(dataFormat.getMime()).build();
		} catch (DatahotelException e) {
			throw e.setFormater(dataFormat);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e, context)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}")
	public Response getDataset(@PathParam("type") String type, @PathParam("location") String location,
			@Context HttpServletRequest req, @Context UriInfo uriInfo) {
		Formater dataFormat = Formater.get(type);
		try {
			Metadata metadata = dataEJB.getChild(location);
			if (metadata == null)
				return returnNotFound("Folder not found or not ready.");

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			if (!metadata.isDataset()) {
				List<MetadataLight> list = dataEJB.getChildren(location);
				if (list == null)
					return returnNotFound("No elements found.");

				RequestContext context = new RequestContext(uriInfo, metadata, null);

				return Response.ok(dataFormat.format(list, context)).type(dataFormat.getMime()).build();
			}

			List<FieldLight> fields = fieldEJB.getFields(metadata);
			RequestContext context = new RequestContext(uriInfo, metadata, fields);

			Result result;
			if (context.isSearch())
				result = searchEJB.find(metadata, context.getQuery(), context.getLookup(), context.getPage());
			else
				result = chunkEJB.get(metadata, context.getPage());

			if (result == null)
				throw new DatahotelException("No data retrieved.");

			return Response.ok(dataFormat.format(result, context)).type(dataFormat.getMime())
					.header("ETag", metadata.getUpdated()).header("X-Datahotel-Page", result.getPage())
					.header("X-Datahotel-Total-Pages", result.getPages())
					.header("X-Datahotel-Total-Posts", result.getPosts()).header("Access-Control-Allow-Origin", "*")
					.build();

		} catch (DatahotelException e) {
			throw e.setFormater(dataFormat);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e, new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		}
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/meta")
	public Response getMeta(@PathParam("type") String type, @PathParam("location") String location,
			@Context HttpServletRequest req, @Context UriInfo uriInfo) {
		Formater dataFormat = Formater.get(type);
		try {
			Metadata metadata = dataEJB.getChild(location);
			if (metadata == null)
				return returnNotFound("Folder not found or not ready.");

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			return Response.ok(dataFormat.format(metadata.light(), new RequestContext(uriInfo, metadata, null)))
					.type(dataFormat.getMime()).header("ETag", metadata.getUpdated())
					.header("Access-Control-Allow-Origin", "*").build();

		} catch (DatahotelException e) {
			throw e.setFormater(dataFormat);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e, new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		}
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/fields")
	public Response getFields(@PathParam("type") String type, @PathParam("location") String location,
			@Context UriInfo uriInfo, @Context HttpServletRequest req) {
		Formater dataFormat = Formater.get(type);
		try {
			Metadata metadata = dataEJB.getChild(location);
			if (metadata == null)
				return returnNotFound("Dataset not found or not ready.");

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			List<FieldLight> fields = fieldEJB.getFields(metadata);
			RequestContext context = new RequestContext(uriInfo, metadata, fields);

			if (fields == null)
				return returnNotFound("Metadata with that name could not be found.");

			return Response.ok(dataFormat.format(fields, context)).type(dataFormat.getMime())
					.header("ETag", metadata.getUpdated()).build();
		} catch (DatahotelException e) {
			throw e.setFormater(dataFormat);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e, null)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}
}
