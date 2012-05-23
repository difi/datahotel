package no.difi.datahotel.client.slave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import no.difi.datahotel.logic.slave.ChunkEJB;
import no.difi.datahotel.logic.slave.DataEJB;
import no.difi.datahotel.logic.slave.FieldEJB;
import no.difi.datahotel.logic.slave.SearchEJB;
import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.bridge.MetadataLight;
import no.difi.datahotel.util.jersey.CSVData;
import no.difi.datahotel.util.jersey.DataFormat;

/**
 * The DataHotelService produces content based on the paths it receives and
 * returns content in accordance with the users requests.
 */
@Path("/")
@Stateless
public class ApiService {

	Logger logger = Logger.getLogger(ApiService.class.getSimpleName());

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
	@Path("{type}")
	public Response getOwnerList(@PathParam("type") String type, @QueryParam("callback") String metadata) {
		DataFormat dataFormat = DataFormat.get(type);
		try {
			List<MetadataLight> list = dataEJB.getChildren();

			if (list == null)
				throw new Exception("No elements found.");

			return Response.ok(dataFormat.format(list, metadata, null)).header("Content-Type", "")
					.type(dataFormat.getMime() + ";charset=UTF-8").build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	@GET
	@Path("{type}/_all")
	public Response getAllDatasets(@PathParam("type") String type, @QueryParam("callback") String metadata) {
		DataFormat dataFormat = DataFormat.get(type);
		try {
			List<MetadataLight> list = new ArrayList<MetadataLight>();
			for (Metadata m : dataEJB.getDatasets())
				list.add(m.light());

			if (list.size() == 0)
				throw new Exception("No elements found.");

			return Response.ok(dataFormat.format(list, metadata, null)).type(dataFormat.getMime() + ";charset=UTF-8")
					.build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	@GET
	@Path("{type}/_defs")
	public Response getAllDefinitions(@PathParam("type") String type, @QueryParam("callback") String metadata) {
		DataFormat dataFormat = DataFormat.get(type);
		try {
			List<Definition> defs = fieldEJB.getDefinitions();
			Collections.sort(defs);

			if (defs.size() == 0)
				throw new Exception("No fields available.");

			return Response.ok(dataFormat.format(defs, metadata, null)).type(dataFormat.getMime() + ";charset=UTF-8")
					.build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	@GET
	@Path("{type}/_defs/{def}")
	public Response getDatasetsByDefinition(@PathParam("type") String type, @PathParam("def") String def,
			@QueryParam("callback") String callback) {
		DataFormat dataFormat = DataFormat.get(type);
		try {
			List<String> datasets = fieldEJB.getUsage(def);
			Collections.sort(datasets);

			if (datasets.size() == 0)
				throw new Exception("Definition never used.");

			return Response.ok(dataFormat.format(datasets, callback, null))
					.type(dataFormat.getMime() + ";charset=UTF-8").build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), callback)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	/**
	 * "/api/type/owner" Gets a list of groups or metadata for an owner in the
	 * datahotel.
	 * 
	 * @param type
	 *            Mime type
	 * @param owner
	 *            Owner
	 * @return
	 */
	@GET
	@Path("{type}/{owner}")
	public Response getGroupListForOwner(@PathParam("type") String type, @PathParam("owner") String owner,
			@QueryParam("callback") String callback) {
		DataFormat dataFormat = DataFormat.get(type);
		try {
			Metadata metadata = dataEJB.getChild(owner);
			if (metadata == null)
				throw new Exception("Group not found.");

			List<MetadataLight> list = dataEJB.getChildren(owner);

			if (list == null)
				throw new Exception("No elements found.");

			return Response.ok(dataFormat.format(list, callback, null)).type(dataFormat.getMime() + ";charset=UTF-8")
					.build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), callback)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	/**
	 * "/api/type/owner/group" Gets a dataset list or metadata for a group.
	 * 
	 * @param type
	 *            Mime type
	 * @param owner
	 *            Owner
	 * @param group
	 *            Group
	 * @return
	 */
	@GET
	@Path("{type}/{owner}/{group}")
	public Response getDatasetListForGroup(@PathParam("type") String type, @PathParam("owner") String owner,
			@PathParam("group") String group, @QueryParam("callback") String callback) {
		DataFormat dataFormat = DataFormat.get(type);
		try {
			Metadata metadata = dataEJB.getChild(owner, group);
			if (metadata == null)
				throw new Exception("Group not found.");

			List<MetadataLight> list = dataEJB.getChildren(owner, group);

			if (list == null)
				throw new Exception("No elements found.");

			return Response.ok(dataFormat.format(list, callback, null)).type(dataFormat.getMime() + ";charset=UTF-8")
					.build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), callback)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	/**
	 * "/api/type/owner/group/dataset" Gets a dataset or metadata of a dataset
	 * based on owner and group.
	 * 
	 * @param type
	 * @param owner
	 * @param group
	 * @param dataset
	 * @param page
	 * @param metadata
	 * @return
	 */
	@GET
	@Path("{type}/{owner}/{group}/{dataset}")
	public Response getDataset(@PathParam("type") String type, @PathParam("owner") String owner,
			@PathParam("group") String group, @PathParam("dataset") String dataset,
			@DefaultValue("1") @QueryParam("page") Integer page, @QueryParam("callback") String callback,
			@DefaultValue("") @QueryParam("query") String query, @Context HttpServletRequest req,
			@Context UriInfo uriInfo) {
		DataFormat dataFormat = DataFormat.get(type);
		try {
			Metadata metadata = dataEJB.getChild(owner, group, dataset);
			if (metadata == null)
				throw new Exception("Dataset not found or not ready.");

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			if (!metadata.isDataset()) {
				List<MetadataLight> list = dataEJB.getChildren();
				if (list == null)
					throw new Exception("No elements found.");

				return Response.ok(dataFormat.format(list, callback, null)).header("Content-Type", "")
						.type(dataFormat.getMime() + ";charset=UTF-8").build();
			}

			List<Field> fields = fieldEJB.getFields(metadata);

			// Look for fields for lookup
			Map<String, String> lookup = new HashMap<String, String>();
			for (Field f : fields)
				if (f.getGroupable())
					if (uriInfo.getQueryParameters().containsKey(f.getShortName()))
						lookup.put(f.getShortName(), uriInfo.getQueryParameters().get(f.getShortName()).get(0));

			if ("".equals(query) && lookup.size() == 0) {
				return Response.ok(dataFormat.format(new CSVData(chunkEJB.get(metadata, page)), callback, fields))
						.type(dataFormat.getMime() + ";charset=UTF-8").header("ETag", metadata.getUpdated())
						.header("X-Datahotel-Page", page)
						.header("X-Datahotel-Total-Pages", chunkEJB.getPages(metadata.getLocation()))
						.header("X-Datahotel-Total-Posts", chunkEJB.getPosts(metadata.getLocation())).build();
			} else {
				return Response.ok(dataFormat.format(new CSVData(searchEJB.find(metadata, query, lookup, page)), callback, fields))
						.type(dataFormat.getMime() + ";charset=UTF-8").header("ETag", metadata.getUpdated())
						.header("X-Datahotel-Page", page).build();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), callback)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	/**
	 * "/api/type/owner/group/dataset" Gets a dataset or metadata of a dataset
	 * based on owner and group.
	 * 
	 * @param type
	 * @param owner
	 * @param group
	 * @param dataset
	 * @param page
	 * @param metadata
	 * @return
	 */
	@GET
	@Path("csv/{owner}/{group}/{dataset}/full")
	public Response getFullDataset(@PathParam("owner") String owner, @PathParam("group") String group,
			@PathParam("dataset") String dataset, @Context HttpServletRequest req) {
		DataFormat dataFormat = DataFormat.CSVCORRECT;
		try {
			Metadata metadata = dataEJB.getChild(owner, group, dataset);

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			return Response.ok(chunkEJB.getFullDataset(metadata)).type(dataFormat.getMime() + ";charset=UTF-8")
					.header("ETag", metadata.getUpdated()).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), null)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	/**
	 * "/api/type/owner/group/dataset/metadata" Gets a dataset or metadata of a
	 * dataset based on owner and group.
	 * 
	 * @param type
	 * @param owner
	 * @param group
	 * @param dataset
	 * @param callback
	 * @return
	 */
	@GET
	@Path("{type}/{owner}/{group}/{dataset}/fields")
	public Response getMetadata(@PathParam("type") String type, @PathParam("owner") String owner,
			@PathParam("group") String group, @PathParam("dataset") String dataset,
			@QueryParam("callback") String callback, @Context HttpServletRequest req) {
		DataFormat dataFormat = DataFormat.get(type);
		try {
			Metadata metadata = dataEJB.getChild(owner, group, dataset);

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			List<Field> fields = fieldEJB.getFields(metadata);

			if (fields == null)
				throw new Exception("Metadata with that name could not be found.");

			return Response.ok(dataFormat.format(fields, callback, fields))
					.type(dataFormat.getMime() + ";charset=UTF-8").header("ETag", metadata.getUpdated()).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), callback)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	private Response returnNotModified() {
		return Response.ok().status(304).build();
	}
}
