package no.difi.datahotel.master.service;

import static no.difi.datahotel.util.shared.Filesystem.FILE_METADATA;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.io.InputStream;
import java.util.List;

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

import no.difi.datahotel.master.logic.DatasetEJB;
import no.difi.datahotel.slave.logic.MetadataEJB;
import no.difi.datahotel.util.model.Metadata;
import no.difi.datahotel.util.model.Version;
import no.difi.datahotel.util.shared.Disk;
import no.difi.datahotel.util.shared.Filesystem;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Stateless
@Path("/master/folder")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DatasetService {

	@EJB
	private MetadataEJB metadataEJB;
	@EJB
	private DatasetEJB datasetEJB;

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
	@Path("{location: [a-z0-9\\-/]*}/children")
	public List<Metadata> getLocation(@PathParam("location") String location) {
		return metadataEJB.getChildren(location);
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}")
	public Metadata getMetadata(@PathParam("location") String location) {
		return metadataEJB.getChild(location);
	}

	@POST
	@Path("{location: [a-z0-9\\-/]*}")
	public void updateMetadata(@PathParam("location") String location, JAXBElement<Metadata> metadataJAXB)
			throws Exception {
		if (metadataEJB.getChild(location) != null) {
			File file = Filesystem.getFile(FOLDER_SLAVE, location, FILE_METADATA);
			Disk.save(file, metadataJAXB.getValue());
		}
	}

	@PUT
	@Path("{location: [a-z0-9\\-/]*}")
	public void insertMetadata(@PathParam("location") String location, JAXBElement<Metadata> metadataJAXB)
			throws Exception {
		if (location.equals("") || metadataEJB.getChild(location) != null) {
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
	public Version uploadFile(@PathParam("location") String location, @FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		Metadata metadata = metadataEJB.getChild(location);
		if (metadata != null && metadata.isDataset()) {
			return datasetEJB.addVersion(metadata, file);
		}

		return null;
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/version")
	public List<Version> getVersions(@PathParam("location") String location) {
		Metadata metadata = metadataEJB.getChild(location);
		if (metadata != null && metadata.isDataset()) {
			return datasetEJB.getVersions(metadata);
		}
		return null;
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/version/{version}")
	public Version getVersion(@PathParam("location") String location, @PathParam("version") String timestamp) {
		Metadata metadata = metadataEJB.getChild(location);
		if (metadata != null && metadata.isDataset()) {
			return datasetEJB.getVersion(metadata, timestamp);
		}
		return null;
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/version/{version}/fields")
	public void getVersionFields(@PathParam("location") String location, @PathParam("version") String timestamp) {
		Metadata metadata = metadataEJB.getChild(location);
		if (metadata != null && metadata.isDataset()) {
			
			// datasetEJB.publishVersion(metadata, timestamp);
		}
	}

	@POST
	@Path("{location: [a-z0-9\\-/]*}/version/{version}/publish")
	public void publishVersion(@PathParam("location") String location, @PathParam("version") String timestamp) {
		Metadata metadata = metadataEJB.getChild(location);
		if (metadata != null && metadata.isDataset()) {
			datasetEJB.publishVersion(metadata, timestamp);
		}
	}
}
