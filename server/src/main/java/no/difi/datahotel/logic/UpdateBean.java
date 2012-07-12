package no.difi.datahotel.logic;

import java.util.logging.Logger;

import no.difi.datahotel.model.Metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("update")
public class UpdateBean {

	@Autowired
	private FieldBean fieldEJB;
	@Autowired
	private ChunkBean chunkEJB;
	@Autowired
	private IndexBean indexEJB;
	@Autowired
	private SearchBean searchEJB;
	@Autowired
	private DataBean dataEJB;

	// TODO How to make this @Asynchronous? Don't want it to be async...
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

	public void setFieldEJB(FieldBean fieldEJB) {
		this.fieldEJB = fieldEJB;
	}

	public void setChunkEJB(ChunkBean chunkEJB) {
		this.chunkEJB = chunkEJB;
	}

	public void setIndexEJB(IndexBean indexEJB) {
		this.indexEJB = indexEJB;
	}

	public void setSearchEJB(SearchBean searchEJB) {
		this.searchEJB = searchEJB;
	}

	public void setDataEJB(DataBean dataEJB) {
		this.dataEJB = dataEJB;
	}
}
