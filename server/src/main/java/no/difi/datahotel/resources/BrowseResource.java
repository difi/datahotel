package no.difi.datahotel.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import no.difi.datahotel.logic.ChunkBean;
import no.difi.datahotel.logic.DataBean;
import no.difi.datahotel.logic.FieldBean;
import no.difi.datahotel.logic.SearchBean;
import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.MetadataLight;
import no.difi.datahotel.model.Result;
import no.difi.datahotel.util.DatahotelException;
import no.difi.datahotel.util.Formater;
import no.difi.datahotel.util.RequestContext;

@Path("/api/{type}/")
@Component
@Scope("request")
public class BrowseResource extends BaseResource {

	Logger logger = Logger.getLogger(BrowseResource.class.getSimpleName());

	@Autowired
	private DataBean dataBean;
	@Autowired
	private FieldBean fieldBean;
	@Autowired
	private ChunkBean chunkBean;
	@Autowired
	private SearchBean searchBean;

	@GET
	public Response getOwnerList(@PathParam("type") String type) {
		return getDataset(type, "");
	}

	@GET
	@Path("_all")
	public Response getAllDatasets(@PathParam("type") String type) {
		Formater dataFormat = Formater.get(type);

		RequestContext context = new RequestContext(uriInfo);

		try {
			List<MetadataLight> list = new ArrayList<MetadataLight>();
			for (Metadata m : dataBean.getDatasets())
				list.add(m.light());

			if (list.size() == 0)
				return returnNotFound("No elements found.");

			return Response.ok(dataFormat.format(list, context)).type(dataFormat.getMime())
					.header("Access-Control-Allow-Origin", "*").build();
		} catch (DatahotelException e) {
			throw e.setFormater(dataFormat);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e, context)).type(dataFormat.getMime()).status(500).build();
		}
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}")
	public Response getDataset(@PathParam("type") String type, @PathParam("location") String location) {
		Formater dataFormat = Formater.get(type);

		try {
			Metadata metadata = dataBean.getChild(location);
			checkNotModified(metadata);

			if (!metadata.isDataset()) {
				List<MetadataLight> list = dataBean.getChildren(location);
				if (list == null)
					return returnNotFound("No elements found.");

				RequestContext context = new RequestContext(uriInfo, metadata, null);

				return Response.ok(dataFormat.format(list, context)).type(dataFormat.getMime()).build();
			}

			List<FieldLight> fields = fieldBean.getFields(metadata);
			RequestContext context = new RequestContext(uriInfo, metadata, fields);

			Result result;
			if (context.isSearch())
				result = searchBean.find(metadata, context.getQuery(), context.getLookup(), context.getPage());
			else
				result = chunkBean.get(metadata, context.getPage());

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
			return Response.ok(dataFormat.formatError(e, new RequestContext(uriInfo))).type(dataFormat.getMime())
					.status(500).build();
		}
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/meta")
	public Response getMeta(@PathParam("type") String type, @PathParam("location") String location) {
		Formater dataFormat = Formater.get(type);

		try {
			Metadata metadata = dataBean.getChild(location);
			checkNotModified(metadata);

			return Response.ok(dataFormat.format(metadata.light(), new RequestContext(uriInfo, metadata, null)))
					.type(dataFormat.getMime()).header("ETag", metadata.getUpdated())
					.header("Access-Control-Allow-Origin", "*").build();

		} catch (DatahotelException e) {
			throw e.setFormater(dataFormat);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e, new RequestContext(uriInfo))).type(dataFormat.getMime())
					.status(500).build();
		}
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/fields")
	public Response getFields(@PathParam("type") String type, @PathParam("location") String location) {
		Formater dataFormat = Formater.get(type);
		try {
	
			Metadata metadata = dataBean.getChild(location);
			checkNotModified(metadata);

			List<FieldLight> fields = fieldBean.getFields(metadata);
			if (fields.size() == 0)
				return returnNotFound("Metadata with that name could not be found.");

			return Response.ok(dataFormat.format(fields, new RequestContext(uriInfo, metadata, fields)))
					.type(dataFormat.getMime()).header("ETag", metadata.getUpdated())
					.header("Access-Control-Allow-Origin", "*").build();
		} catch (DatahotelException e) {
			throw e.setFormater(dataFormat);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e, null)).type(dataFormat.getMime()).status(500).build();
		}
	}

	public void setDataEJB(DataBean dataEJB) {
		this.dataBean = dataEJB;
	}

	public void setFieldEJB(FieldBean fieldEJB) {
		this.fieldBean = fieldEJB;
	}

	public void setChunkEJB(ChunkBean chunkEJB) {
		this.chunkBean = chunkEJB;
	}

	public void setSearchEJB(SearchBean searchEJB) {
		this.searchBean = searchEJB;
	}
}
