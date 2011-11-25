package no.difi.datahotel.client.slave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import no.difi.datahotel.logic.slave.ChunkEJB;
import no.difi.datahotel.logic.slave.MetadataEJB;
import no.difi.datahotel.logic.slave.SearchEJB;
import no.difi.datahotel.logic.slave.StructureEJB;
import no.difi.datahotel.util.bridge.Dataset;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Group;
import no.difi.datahotel.util.bridge.Owner;
import no.difi.datahotel.util.jersey.CSVData;
import no.difi.datahotel.util.jersey.DataFormat;

/**
 * The DataHotelService produces content based on the paths it receives and
 * returns content in accordance with the users requests.
 */
@Path("/")
@Stateless
public class ApiService {

	@EJB
	private MetadataEJB metadataEJB;
	@EJB
	private StructureEJB structureEJB;
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
		DataFormat dataFormat = DataFormat.PLAIN_TEXT;
		try {
			dataFormat = DataFormat.get(type);
			List<Owner> ownerList = metadataEJB.getOwners();

			if (ownerList.size() == 0)
				throw new Exception("No owners could be found.");

			return Response.ok(dataFormat.format(ownerList, metadata)).header("Content-Type", "").type(dataFormat.getMime() + ";charset=UTF-8").build();
		} catch (Exception e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
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
			@QueryParam("callback") String metadata) {
		DataFormat dataFormat = DataFormat.PLAIN_TEXT;
		try {
			dataFormat = DataFormat.get(type);
			List<Group> groupList = metadataEJB.getGroups(owner);

			if (groupList.size() == 0)
				throw new Exception("Groups for this owner could not be found.");

			return Response.ok(dataFormat.format(groupList, metadata)).type(dataFormat.getMime() + ";charset=UTF-8").build();
		} catch (Exception e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
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
			@PathParam("group") String group, @QueryParam("callback") String metadata) {
		DataFormat dataFormat = DataFormat.PLAIN_TEXT;
		try {
			dataFormat = DataFormat.get(type);
			List<Dataset> datasets = metadataEJB.getDatasets(owner, group);

			if (datasets.size() == 0)
				throw new Exception("Datasets for this owner and group could not be found.");

			return Response.ok(dataFormat.format(datasets, metadata)).type(dataFormat.getMime() + ";charset=UTF-8").build();
		} catch (Exception e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
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
			@DefaultValue("1") @QueryParam("page") Integer page, @QueryParam("callback") String metadata) {
		DataFormat dataFormat = DataFormat.PLAIN_TEXT;
		try {
			dataFormat = DataFormat.get(type);
			long timestamp = structureEJB.getTimestamp(owner, group, dataset);
			
			// TODO Check "If-None-Match"-header.

			CSVData csvData = new CSVData(chunkEJB.get(owner, group, dataset, page));

			return Response.ok(dataFormat.format(csvData, metadata)).type(dataFormat.getMime() + ";charset=UTF-8").header("ETag", timestamp).build();
		} catch (Exception e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
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
	 * @param metadata
	 * @return
	 */
	@GET
	@Path("{type}/{owner}/{group}/{dataset}/fields")
	public Response getMetadata(@PathParam("type") String type, @PathParam("owner") String owner,
			@PathParam("group") String group, @PathParam("dataset") String dataset,
			@QueryParam("callback") String metadata) {
		DataFormat dataFormat = DataFormat.PLAIN_TEXT;
		try {
			dataFormat = DataFormat.get(type);

			List<Field> fields = metadataEJB.getFields(owner, group, dataset).getFields();

			if (fields == null)
				throw new Exception("Metadata with that name could not be found.");

			return Response.ok(dataFormat.format(fields, metadata)).type(dataFormat.getMime() + ";charset=UTF-8").build();
		} catch (Exception e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	@GET
	@Path("{type}/{owner}/{group}/{dataset}/search")
	public Response getSearch(@PathParam("type") String type, @PathParam("owner") String owner,
			@PathParam("group") String group, @PathParam("dataset") String dataset, @DefaultValue("") @QueryParam("query") String query,
			@QueryParam("callback") String metadata) {
		DataFormat dataFormat = DataFormat.PLAIN_TEXT;
		try {
			dataFormat = DataFormat.get(type);

			Object results;
			
			if (!query.trim().isEmpty())
				results = new CSVData(searchEJB.find(owner, group, dataset, query));
			else
			{
				List<Field> fields = metadataEJB.getFields(owner, group, dataset).getFields();
				List<Field> res = new ArrayList<Field>();
				for (Field f : fields)
					if (f.getSearchable())
						res.add(f);
				results = res;
			}

			return Response.ok(dataFormat.format(results, metadata)).type(dataFormat.getMime() + ";charset=UTF-8").build();
		} catch (Exception e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

	@GET
	@Path("{type}/{owner}/{group}/{dataset}/lookup")
	public Response getLookupMulti(@PathParam("type") String type, @PathParam("owner") String owner,
			@PathParam("group") String group, @PathParam("dataset") String dataset,
			@QueryParam("callback") String metadata, @Context UriInfo uriInfo) {

		DataFormat dataFormat = DataFormat.PLAIN_TEXT;
		try {
			dataFormat = DataFormat.get(type);

			Map<String, String> query = new HashMap<String, String>();
			List<Field> fields = metadataEJB.getFields(owner, group, dataset).getFields();

			for (Field f : fields)
				if (f.getGroupable())
					if (uriInfo.getQueryParameters().containsKey(f.getShortName()))
						query.put(f.getShortName(), uriInfo.getQueryParameters().get(f.getShortName()).get(0));

			Object results;
			
			if (query.size() > 0)
				results = new CSVData(searchEJB.lookup(owner, group, dataset, query));
			else
			{
				List<Field> res = new ArrayList<Field>();
				for (Field f : fields)
					if (f.getGroupable())
						res.add(f);
				results = res;
			}

			return Response.ok(dataFormat.format(results, metadata)).type(dataFormat.getMime() + ";charset=UTF-8").build();
		} catch (Exception e) {
			return Response.ok(dataFormat.formatError(e.getMessage(), metadata)).type(dataFormat.getMime()).status(500)
					.build();
		}
	}

}
