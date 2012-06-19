package no.difi.datahotel.slave.logic;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import no.difi.datahotel.model.Metadata;

@Stateless
public class UpdateEJB {

	@EJB
	private FieldEJB fieldEJB;
	@EJB
	private ChunkEJB chunkEJB;
	@EJB
	private IndexEJB indexEJB;
	@EJB
	private SearchEJB searchEJB;
	@EJB
	private DataEJB dataEJB;

	// TODO How to make this @Asynchronous?
	public void validate(Metadata metadata) {
		Logger logger = metadata.getLogger();

		if (metadata.getUpdated() == null) {
			logger.warning("Missing timestamp in metadata file.");
			return;
		}

		if (metadata.getUpdated().equals(dataEJB.getTimestamp(metadata.getLocation())))
			return;

		if (dataEJB.getTimestamp(metadata.getLocation()) != null && dataEJB.getTimestamp(metadata.getLocation()) == -1) {
			logger.info("Do not disturb.");
			return;
		}

		dataEJB.setTimestamp(metadata.getLocation(), -1L);

		fieldEJB.update(metadata);
		chunkEJB.update(metadata);
		indexEJB.update(metadata);
		searchEJB.update(metadata);

		logger.info("Ready");
		dataEJB.setTimestamp(metadata.getLocation(), metadata.getUpdated());
	}
}
