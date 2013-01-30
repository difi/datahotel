package no.difi.datahotel.master.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import no.difi.datahotel.master.logic.MasterDefinitionBean;
import no.difi.datahotel.model.Definition;

@Path("/master/definition")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
@Scope("request")
public class MasterDefinitionResource {

	private MasterDefinitionBean definitionBean;

	@GET
	public List<Definition> getDefinitions() {
		return definitionBean.getDefinitions();
	}

	@POST
	public void updateDefinition(JAXBElement<Definition> definitionJAXB) throws Exception {
		definitionBean.setDefinition(definitionJAXB.getValue());
	}

	@GET
	@Path("{definition}")
	public Definition getDefinition(@PathParam("definition") String definition) {
		return definitionBean.getDefinition(definition);
	}

	@Autowired
	public void setDefinitionEJB(MasterDefinitionBean definitionEJB) {
		this.definitionBean = definitionEJB;
	}
}
