package no.difi.datahotel.master.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import no.difi.datahotel.master.logic.DefinitionEJB;
import no.difi.datahotel.util.bridge.Definition;

@Stateless
@Path("/definition/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DefinitionService {

	@EJB
	private DefinitionEJB definitionEJB;

	@GET
	public List<Definition> getDefinitions() {
		return definitionEJB.getDefinitions();
	}

	@POST
	public void updateDefinition(JAXBElement<Definition> definitionJAXB) throws Exception {
		definitionEJB.setDefinition(definitionJAXB.getValue());
	}

	@GET
	@Path("{definition}")
	public Definition getDefinition(@PathParam("definition") String definition) {
		return definitionEJB.getDefinition(definition);
	}

}
