package no.difi.datahotel.slave.logic;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class StartupEJB {

	@EJB
	private MetadataEJB metadataEJB;

	@PostConstruct
	public void init() {
		metadataEJB.update();
	}
}
