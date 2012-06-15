package no.difi.datahotel.master.service;

import static no.difi.datahotel.util.shared.Filesystem.FILE_METADATA;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import no.difi.datahotel.slave.logic.MetadataEJB;
import no.difi.datahotel.util.model.Disk;
import no.difi.datahotel.util.model.Metadata;
import no.difi.datahotel.util.model.Version;
import no.difi.datahotel.util.shared.Filesystem;
import no.difi.datahotel.util.shared.Part;

import org.apache.commons.io.IOUtils;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Stateless
@Path("/master/folder")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FolderService {

	@EJB
	private MetadataEJB metadataEJB;

	@PUT
	public void insertMetadata(JAXBElement<Metadata> metadataJAXB) throws Exception {
		insertMetadata("", metadataJAXB);
	}

	@GET
	@Path("children")
	public List<Metadata> getRoot() {
		return getLocation("");
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}")
	public Metadata getMetadata(@PathParam("location") String location) {
		return metadataEJB.getChild(Part.SLAVE, location);
	}

	@POST
	@Path("{location: [a-z0-9\\-/]*}")
	public void updateMetadata(@PathParam("location") String location, JAXBElement<Metadata> metadataJAXB)
			throws Exception {
		if (metadataEJB.getChild(Part.SLAVE, location) != null) {
			File file = Filesystem.getFile(FOLDER_SLAVE, location, FILE_METADATA);
			Disk.save(file, metadataJAXB.getValue());
		}
	}

	@PUT
	@Path("{location: [a-z0-9\\-/]*}")
	public void insertMetadata(@PathParam("location") String location, JAXBElement<Metadata> metadataJAXB)
			throws Exception {
		if (location.equals("") || metadataEJB.getChild(Part.SLAVE, location) != null) {
			File file = Filesystem.getFile(FOLDER_SLAVE, location, metadataJAXB.getValue().getShortName(),
					FILE_METADATA);
			Disk.save(file, metadataJAXB.getValue());
		}
	}

	// curl -X POST --form file=@file
	// http://localhost:8080/master/folder/[location]/upload
	@POST
	@Path("{location: [a-z0-9\\-/]*}/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadFile(@PathParam("location") String location, @FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		Metadata metadata = metadataEJB.getChild(Part.SLAVE, location);
		Logger logger = metadata.getLogger();

		if (metadata != null && metadata.isDataset()) {
			try {
				metadata.getLogger().info(fileDetail.getFileName());
				String ts = String.valueOf(System.currentTimeMillis());
				File target = Filesystem.getFile(Filesystem.FOLDER_MASTER, metadata.getLocation(), ts, "original.csv");
				FileWriter writer = new FileWriter(target);
				IOUtils.copy(file, writer);
				writer.close();

				logger.info("Uploaded file.");
				return ts;
			} catch (Exception e) {}
		}

		logger.warning("Unable to upload file.");
		return "Failed\n";
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/versions")
	public List<Version> getVersions(@PathParam("location") String location) {
		Metadata metadata = metadataEJB.getChild(Part.SLAVE, location);
		if (metadata != null && metadata.isDataset()) {
			File home = Filesystem.getFolder(Filesystem.FOLDER_MASTER, metadata.getLocation());
			List<Version> versions = new ArrayList<Version>();
			for (File f : home.listFiles())
				if (f.isDirectory())
					versions.add(new Version(f));
			return versions;
		}
		return null;
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/children")
	public List<Metadata> getLocation(@PathParam("location") String location) {
		return metadataEJB.getChildren(Part.SLAVE, location);
	}
}
