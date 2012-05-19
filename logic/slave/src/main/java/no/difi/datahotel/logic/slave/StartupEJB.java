package no.difi.datahotel.logic.slave;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class StartupEJB {

	private static Logger logger = Logger.getLogger(StartupEJB.class.getSimpleName());

	@EJB
	private SystemEJB systemEJB;
	@EJB
	private Metadata2EJB metadata2ejb;
	
	@PostConstruct
	public void run() {
		logger.info("Datahotel startup");

		// logger.info("Loading structure...");
		// structureEJB.update();
		
		// logger.info("Loading metadata...");
		// metadataEJB.update();
		
		logger.info("Loading metadata2...");
		metadata2ejb.update();

		logger.info("Allow schedulers to run");
		systemEJB.setReady(true);
	}
}