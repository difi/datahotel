package no.difi.datahotel.client.slave;

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

import no.difi.datahotel.logic.slave.ChunkEJB;
import no.difi.datahotel.logic.slave.DataEJB;
import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.jersey.DataFormat;

@Path("/download/")
@Stateless
public class DownloadService extends BaseService {

	Logger logger = Logger.getLogger(DownloadService.class.getSimpleName());

	@EJB
	private DataEJB dataEJB;
	@EJB
	private ChunkEJB chunkEJB;

	/**
	 * "/api/type/owner/group/dataset" Gets a dataset or metadata of a dataset
	 * based on owner and group.
	 * 
	 * @param type
	 * @param location
	 * @return
	 */
	@GET
	@Path("{location: [a-z0-9\\-/]*}")
	public Response getFullDataset(@PathParam("location") String location, @Context HttpServletRequest req) {
		DataFormat dataFormat = DataFormat.CSVCORRECT;
		try {
			Metadata metadata = dataEJB.getChild(location);
			if (metadata == null)
				return returnNotFound("Dataset not found or not ready.");

			if (String.valueOf(metadata.getUpdated()).equals(req.getHeader("If-None-Match")))
				return returnNotModified();

			return Response.ok(chunkEJB.getFullDataset(metadata)).type(dataFormat.getMime())
					.header("ETag", metadata.getUpdated()).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return Response.ok(dataFormat.formatError(e.getMessage(), null)).type(dataFormat.getMime()).status(404)
					.build();
		}
	}
}
