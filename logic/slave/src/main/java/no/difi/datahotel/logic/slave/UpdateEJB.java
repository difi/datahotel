package no.difi.datahotel.logic.slave;

import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import no.difi.datahotel.util.bridge.Metadata;

@Stateless
public class UpdateEJB {

	private static Logger logger = Logger.getLogger(UpdateEJB.class.getSimpleName());

	@EJB
	private FieldEJB fieldEJB;
	@EJB
	private ChunkEJB chunkEJB;
	@EJB
	private IndexEJB indexEJB;
	@EJB
	private DataEJB dataEJB;

	@Asynchronous
	public void validate(Metadata metadata) {
		if (metadata.getUpdated() == null) {
			logger.warning("[" + metadata.getLocation() + "] Missing timestamp in metadata file.");
			return;
		}

		if (metadata.getUpdated().equals(dataEJB.getTimestamp(metadata.getLocation())))
			return;

		if (dataEJB.getTimestamp(metadata.getLocation()) != null && dataEJB.getTimestamp(metadata.getLocation()) == -1) {
			logger.info("[" + metadata.getLocation() + "] Do not disturb.");
			return;
		}

		dataEJB.setTimestamp(metadata.getLocation(), -1L);

		fieldEJB.update(metadata);
		chunkEJB.update(metadata);
		indexEJB.update(metadata);

		logger.info("[" + metadata.getLocation() + "] Ready");
		dataEJB.setTimestamp(metadata.getLocation(), metadata.getUpdated());
	}
}
