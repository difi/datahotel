package no.difi.datahotel.slave.resources;

import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.slave.logic.ChunkBean;
import no.difi.datahotel.slave.logic.DataBean;
import no.difi.datahotel.util.DatahotelException;
import no.difi.datahotel.util.Formater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/download/")
@Component
@Scope("request")
public class DownloadResource extends BaseResource {

    private static Logger logger = Logger.getLogger(DownloadResource.class.getSimpleName());

    @Autowired
    private DataBean dataBean;

    @Autowired
    private ChunkBean chunkBean;

    @GET
    @Path("{location: [a-z0-9\\-/]*}")
    public Response getFullDataset(@PathParam("location") String location) {
        Formater dataFormat = Formater.CSVCORRECT;

        Metadata metadata = dataBean.getChild(location);
        checkNotModified(metadata);
        try {
            if (uriInfo.getQueryParameters().containsKey("excel"))
                // http://www.progresstalk.com/threads/can-i-force-excel-to-automatically-open-a-utf-8-csv-file-correctly.122183/
                return Response.ok(chunkBean.getFullDataset(metadata)).type("application~/vnd.ms-excel~; charset=UTF-8").header("Content-Disposition", "Attachment;filename=" + metadata.getShortName() + ".csv").header("ETag", metadata.getUpdated()).build();
            else if (uriInfo.getQueryParameters().containsKey("download"))
                return Response.ok(chunkBean.getFullDataset(metadata)).type(dataFormat.getMime()).header("Content-Disposition", "Attachment;filename=" + metadata.getShortName() + ".csv").header("ETag", metadata.getUpdated()).build();
            else
                return Response.ok(chunkBean.getFullDataset(metadata)).type(dataFormat.getMime()).header("ETag", metadata.getUpdated()).build();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new DatahotelException(e.getMessage()).setFormater(dataFormat);
        }
    }

    @GET
    @Path("{location: [a-z0-9\\-/]*}/meta.xml")
    public Response getMetadata(@PathParam("location") String location) {
        Formater dataFormat = Formater.XML;

        Metadata metadata = dataBean.getChild(location);
        checkNotModified(metadata);

        try {
            return Response.ok(chunkBean.getMetadata(metadata)).type(dataFormat.getMime()).header("ETag", metadata.getUpdated()).build();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new DatahotelException(e.getMessage()).setFormater(dataFormat);
        }
    }

    @GET
    @Path("{location: [a-z0-9\\-/]*}/fields.xml")
    public Response getFields(@PathParam("location") String location) {
        Formater dataFormat = Formater.XML;

        Metadata metadata = dataBean.getChild(location);
        checkNotModified(metadata);
        try {
            return Response.ok(chunkBean.getFields(metadata)).type(dataFormat.getMime()).header("ETag", metadata.getUpdated()).build();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new DatahotelException(e.getMessage()).setFormater(dataFormat);
        }
    }

    public void setDataEJB(DataBean dataEJB) {
        this.dataBean = dataEJB;
    }

    public void setChunkEJB(ChunkBean chunkEJB) {
        this.chunkBean = chunkEJB;
    }
}
