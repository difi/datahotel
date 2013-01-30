package no.difi.datahotel.slave.logic;

import java.util.logging.Logger;

import no.difi.datahotel.model.Metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("update")
public class UpdateBean {

	@Autowired
	private FieldBean fieldBean;
	@Autowired
	private ChunkBean chunkBean;
	@Autowired
	private IndexBean indexBean;
	@Autowired
	private SearchBean searchBean;
	@Autowired
	private DataBean dataBean;

	public void validate(Metadata metadata) {
		Logger logger = metadata.getLogger();

		if (metadata.getUpdated() == null) {
			logger.warning("Missing timestamp in metadata file.");
			return;
		}

		if (metadata.getUpdated().equals(dataBean.getTimestamp(metadata.getLocation())))
			return;

		if (dataBean.getTimestamp(metadata.getLocation()) != null && dataBean.getTimestamp(metadata.getLocation()) == -1) {
			logger.info("Do not disturb.");
			return;
		}

		dataBean.setTimestamp(metadata.getLocation(), -1L);

		fieldBean.update(metadata);
		chunkBean.update(metadata);
		indexBean.update(metadata);
		searchBean.update(metadata);

		logger.info("Ready");
		dataBean.setTimestamp(metadata.getLocation(), metadata.getUpdated());
	}

	public void setFieldBean(FieldBean fieldBean) {
		this.fieldBean = fieldBean;
	}

	public void setChunkBean(ChunkBean chunkBean) {
		this.chunkBean = chunkBean;
	}

	public void setIndexBean(IndexBean indexBean) {
		this.indexBean = indexBean;
	}

	public void setSearchBean(SearchBean searchBean) {
		this.searchBean = searchBean;
	}

	public void setDataBean(DataBean dataBean) {
		this.dataBean = dataBean;
	}
}
