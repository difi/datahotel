package no.difi.datahotel.slave.resources;

import no.difi.datahotel.model.DefinitionLight;
import no.difi.datahotel.model.Field;
import no.difi.datahotel.slave.logic.FieldBean;
import no.difi.datahotel.util.DatahotelException;
import no.difi.datahotel.util.Formater;
import no.difi.datahotel.util.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/api/{type}/_def/")
@Component
@Scope("request")
public class DefinitionResource extends BaseResource {

    private static Logger logger = Logger.getLogger(DefinitionResource.class.getSimpleName());

    @Autowired
	private FieldBean fieldBean;

	@GET
	public Response getDefinitions(@PathParam("type") String type) {
		Formater dataFormat = Formater.get(type);
		RequestContext context = new RequestContext(uriInfo);

		try {
			List<DefinitionLight> defs = fieldBean.getDefinitions();
			Collections.sort(defs);

			if (defs.size() == 0)
				return returnNotFound("No definitions available.");

			return Response.ok(dataFormat.format(defs, context)).type(dataFormat.getMime()).build();
		} catch (DatahotelException e) {
			throw e.setFormater(dataFormat);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			throw new DatahotelException(e.getMessage()).setFormater(dataFormat);
		}
	}

	@GET
	@Path("{def}")
	public Response getDefinition(@PathParam("type") String type, @PathParam("def") String def) {
		Formater dataFormat = Formater.get(type);
		RequestContext context = new RequestContext(uriInfo);

		try {
			List<String> datasets = new ArrayList<String>();
			for (Field f : fieldBean.getUsage(def))
				if (!datasets.contains(f.getMetadata().getLocation()))
					datasets.add(f.getMetadata().getLocation());
			Collections.sort(datasets);

			if (datasets.size() == 0)
				return returnNotFound("Definition never used.");

			return Response.ok(dataFormat.format(datasets, context)).type(dataFormat.getMime()).build();
		} catch (DatahotelException e) {
			throw e.setFormater(dataFormat);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			throw new DatahotelException(e.getMessage()).setFormater(dataFormat);
		}
	}

	public void setFieldEJB(FieldBean fieldEJB) {
		this.fieldBean = fieldEJB;
	}
}
