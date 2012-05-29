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

import no.difi.datahotel.slave.logic.ChunkEJB;
import no.difi.datahotel.slave.logic.DataEJB;
import no.difi.datahotel.slave.logic.FieldEJB;
import no.difi.datahotel.slave.logic.SearchEJB;
import no.difi.datahotel.util.jersey.CSVData;
import no.difi.datahotel.util.jersey.DataFormat;
import no.difi.datahotel.util.jersey.RequestContext;
import no.difi.datahotel.util.model.FieldLight;
import no.difi.datahotel.util.model.Metadata;
import no.difi.datahotel.util.model.MetadataLight;
import no.difi.datahotel.util.shared.DatahotelException;

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

	/**
	 * "/api/type" Gets a list of owners available in the datahotel.
	 * 
	 * @param type
	 *            Mime type
	 * @return
	 */
	@GET
	// @Path("")
	public Response getOwnerList(@PathParam("type") String type, @Context HttpServletRequest req,
			@Context UriInfo uriInfo) {
		return getDataset(type, "", req, uriInfo);
	}

	@GET
	@Path("_all")
	public Response getAllDatasets(@PathParam("type") String type, @Context UriInfo uriInfo) {
		DataFormat dataFormat = DataFormat.get(type);
		RequestContext context = new RequestContext(uriInfo);

		try {
			List<MetadataLight> list = new ArrayList<MetadataLight>();
			for (Metadata m : dataEJB.getDatasets())
				list.add(m.light());

			if (list.size() == 0)
				return returnNotFound("No elements found.");

			return Response.ok(dataFormat.format(list, context)).type(dataFormat.getMime()).build();
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
	 * "/api/type/owner/group/dataset" Gets a dataset or metadata of a dataset
	 * based on owner and group.
	 * 
	 * @param type
	 * @return
	 */
	@GET
	@Path("{location: [a-z0-9\\-/]*}")
	public Response getDataset(@PathParam("type") String type, @PathParam("location") String location,
			@Context HttpServletRequest req, @Context UriInfo uriInfo) {
		DataFormat dataFormat = DataFormat.get(type);
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

			if (context.isSearch()) {
				return Response
						.ok(dataFormat.format(
								new CSVData(searchEJB.find(metadata, context.getQuery(), context.getLookup(),
										context.getPage())), context)).type(dataFormat.getMime())
						.header("ETag", metadata.getUpdated()).header("X-Datahotel-Page", context.getPage()).build();
			} else {
				return Response.ok(dataFormat.format(new CSVData(chunkEJB.get(metadata, context.getPage())), context))
						.type(dataFormat.getMime()).header("ETag", metadata.getUpdated())
						.header("X-Datahotel-Page", context.getPage())
						.header("X-Datahotel-Total-Pages", chunkEJB.getPages(metadata.getLocation()))
						.header("X-Datahotel-Total-Posts", chunkEJB.getPosts(metadata.getLocation())).build();
			}
		} catch (DatahotelException e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		}
	}

	/**
	 * "/api/type/owner/group/dataset/metadata" Gets a dataset or metadata of a
	 * dataset based on owner and group.
	 * 
	 * @param type
	 * @return
	 */
	@GET
	@Path("{location: [a-z0-9\\-/]*}/fields")
	public Response getFields(@PathParam("type") String type, @PathParam("location") String location,
			@Context UriInfo uriInfo, @Context HttpServletRequest req) {
		DataFormat dataFormat = DataFormat.get(type);
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
			return Response.ok(dataFormat.formatError(e.getMessage(), new RequestContext(uriInfo)))
					.type(dataFormat.getMime()).status(500).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), null)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}
}
