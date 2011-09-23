package no.difi.datahotel.client.master;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import no.difi.datahotel.logic.impl.DatasetEJB;
import no.difi.datahotel.logic.model.DatasetEntity;

@Path("/")
@Stateless
public class DatasetService {

	@EJB
	private DatasetEJB datasetEJB;

	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_XML })
	public List<DatasetEntity> getAll() {
		return datasetEJB.getAll();
	}
}
