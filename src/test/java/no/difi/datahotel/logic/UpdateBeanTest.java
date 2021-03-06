package no.difi.datahotel.logic;

import no.difi.datahotel.BaseTest;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.MetadataLogger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class UpdateBeanTest extends BaseTest {

    private UpdateBean updateBean;

    private FieldBean fieldBean;
    private ChunkBean chunkBean;
    private IndexBean indexBean;
    private SearchBean searchBean;
    private DataBean dataBean;
    private MetadataLogger logger;
    private Metadata metadata;

    @Before
    public void before() throws Exception {
        fieldBean = Mockito.mock(FieldBean.class);
        chunkBean = Mockito.mock(ChunkBean.class);
        indexBean = Mockito.mock(IndexBean.class);
        searchBean = Mockito.mock(SearchBean.class);
        dataBean = new DataBean();
        logger = Mockito.mock(MetadataLogger.class);

        updateBean = new UpdateBean();
        updateBean.setFieldBean(fieldBean);
        updateBean.setChunkBean(chunkBean);
        updateBean.setIndexBean(indexBean);
        updateBean.setSearchBean(searchBean);
        updateBean.setDataBean(dataBean);

        metadata = new Metadata();
        metadata.setLocation("difi/geo/fylke");
        metadata.setLogger(logger);
    }

    @Test
    public void testTriggerMissingTimestamp() {
        updateBean.validate(metadata);

        verify(logger).warning("Missing timestamp in metadata file.");
    }

    @Test
    public void testTriggerUpdating() {
        dataBean.setTimestamp(metadata.getLocation(), -1L);
        metadata.setUpdated(10L);

        updateBean.validate(metadata);

        verify(logger).info("Do not disturb.");
    }

    @Test
    public void testNoAction() {
        dataBean.setTimestamp(metadata.getLocation(), 10L);
        metadata.setUpdated(10L);

        updateBean.validate(metadata);

        verifyZeroInteractions(logger);
    }

    @Test
    public void testNormalUpdateFirstTime() {
        metadata.setUpdated(10L);

        updateBean.validate(metadata);

        verify(fieldBean).update(metadata);
        verify(chunkBean).update(metadata);
        verify(indexBean).update(metadata);
        verify(searchBean).update(metadata);
        verify(logger).info("Ready");
    }

    @Test
    public void testNormalUpdateTimestampHigher() {
        dataBean.setTimestamp(metadata.getLocation(), 5L);
        metadata.setUpdated(10L);

        updateBean.validate(metadata);

        verify(fieldBean).update(metadata);
        verify(chunkBean).update(metadata);
        verify(indexBean).update(metadata);
        verify(searchBean).update(metadata);
        verify(logger).info("Ready");
    }

    @Test
    public void testNormalUpdateTimestampLower() {
        dataBean.setTimestamp(metadata.getLocation(), 15L);
        metadata.setUpdated(10L);

        updateBean.validate(metadata);

        verify(fieldBean).update(metadata);
        verify(chunkBean).update(metadata);
        verify(indexBean).update(metadata);
        verify(searchBean).update(metadata);
        verify(logger).info("Ready");
    }
}
