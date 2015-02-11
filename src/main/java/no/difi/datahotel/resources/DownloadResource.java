package no.difi.datahotel.resources;

import no.difi.datahotel.logic.ChunkBean;
import no.difi.datahotel.logic.DataBean;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.DatahotelException;
import no.difi.datahotel.util.Formater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            if (uriInfo.getQueryParameters().containsKey("excel")) {
                List<InputStream> streams = new ArrayList<InputStream>();
                streams.add(new ByteArrayInputStream(new byte[]{ (byte) 65279 }));
                streams.add(new FileInputStream(chunkBean.getFullDataset(metadata)));
                InputStream result = new SequenceInputStream(Collections.enumeration(streams));

                return Response.ok(result).type("application~/vnd.ms-excel~; charset=UTF-8").header("Content-Disposition", "Attachment;filename=" + metadata.getShortName() + ".csv").header("ETag", metadata.getUpdated()).build();
            }
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
