package no.difi.datahotel.master.service;

import static no.difi.datahotel.util.shared.Filesystem.FILE_METADATA;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SLAVE;

import java.io.File;
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

import no.difi.datahotel.slave.logic.MetadataEJB;
import no.difi.datahotel.util.model.Disk;
import no.difi.datahotel.util.model.Metadata;
import no.difi.datahotel.util.shared.Filesystem;
import no.difi.datahotel.util.shared.Part;

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
	public void updateMetadata(@PathParam("location") String location, JAXBElement<Metadata> metadataJAXB) throws Exception {
		if (metadataEJB.getChild(Part.SLAVE, location) != null) {
			File file = Filesystem.getFile(FOLDER_SLAVE, location, FILE_METADATA);
			Disk.save(file, metadataJAXB.getValue());
		}
	}

	@PUT
	@Path("{location: [a-z0-9\\-/]*}")
	public void insertMetadata(@PathParam("location") String location, JAXBElement<Metadata> metadataJAXB) throws Exception {
		if (location.equals("") || metadataEJB.getChild(Part.SLAVE, location) != null) {
			File file = Filesystem.getFile(FOLDER_SLAVE, location, metadataJAXB.getValue().getShortName(), FILE_METADATA);
			Disk.save(file, metadataJAXB.getValue());
		}
	}

	@GET
	@Path("{location: [a-z0-9\\-/]*}/children")
	public List<Metadata> getLocation(@PathParam("location") String location) {
		return metadataEJB.getChildren(Part.SLAVE, location);
	}

}
