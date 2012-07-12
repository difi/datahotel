package no.difi.datahotel.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



// @Singleton
// @Startup
@Component
public class StartupBean {

	@Autowired
	private MetadataBean metadataEJB;

	// @PostConstruct
	public void init() {
		metadataEJB.update();
	}
}
