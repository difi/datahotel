package no.difi.datahotel.logic;

import no.difi.datahotel.BaseTest;
import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.CSVReader;
import no.difi.datahotel.util.Filesystem;
import no.difi.datahotel.util.MetadataLogger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.difi.datahotel.util.Filesystem.FILE_DATASET;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class IndexBeanTest extends BaseTest {

    private Metadata metadata;

    private IndexBean indexBean;
    private FieldBean fieldBean;
    private SearchBean searchBean;

    private MetadataLogger logger;

    @Before
    public void before() throws Exception {
        fieldBean = new FieldBean();
        logger = Mockito.mock(MetadataLogger.class);

        indexBean = new IndexBean();
        indexBean.setFielBean(fieldBean);

        searchBean = new SearchBean();

        metadata = new Metadata();
        metadata.setLocation("difi/miljo/kalkulator");
        metadata.setUpdated(System.currentTimeMillis());
        metadata.setLogger(logger);
    }

    @Test
    public void testIndex() throws Exception {
        fieldBean.update(metadata);
        indexBean.update(metadata);
        searchBean.update(metadata);

        verify(logger).info("Reading fields.");
        verify(logger).info("Building index.");
    }

    @Test
    public void testNoSource() {
        metadata.setLocation("difi/miljo/no-exists");
        metadata.setLogger(logger);

        indexBean.update(metadata);
        searchBean.update(metadata);

        // TODO Verify logger.
    }

    @Test
    public void testNoIndex() {
        metadata.setLocation("no/dataset/here");
        metadata.setLogger(logger);
        assertEquals(0, searchBean.find(metadata, "kings", null, 1).getEntries().size());

        // TODO Verify logger.
    }

    @Test
    public void testSearch() throws Exception {
        testIndex();

        assertEquals(0, searchBean.find(metadata, "9908:*", null, 1).getEntries().size());

        assertEquals(2, searchBean.find(metadata, "Energi", null, 1).getEntries().size());
        assertEquals(0, searchBean.find(metadata, "km", null, 1).getEntries().size());
        assertEquals(1, searchBean.find(metadata, "tog", null, 1).getEntries().size());
        assertEquals(1, searchBean.find(metadata, "ark", null, 1).getEntries().size());
        assertEquals(2, searchBean.find(metadata, "BUSS", null, 1).getEntries().size());
        assertEquals(2, searchBean.find(metadata, "BU*", null, 1).getEntries().size());

        assertEquals(0, searchBean.find(metadata, "Energi", null, 2).getEntries().size());
        assertEquals(0, searchBean.find(metadata, "km", null, 2).getEntries().size());
        assertEquals(0, searchBean.find(metadata, "tog", null, 2).getEntries().size());
        assertEquals(0, searchBean.find(metadata, "ark", null, 2).getEntries().size());
        assertEquals(0, searchBean.find(metadata, "BUSS", null, 2).getEntries().size());
    }

    @Test
    @Ignore
    public void testEnhetsregisteret() throws Exception {
        metadata.setLocation("brreg/enhetsregisteret");

        indexBean.update(metadata);
        searchBean.update(metadata);

        Map<String, String> query;

        query = new HashMap<String, String>();
        query.put("organisasjonsform", "BA");
        assertEquals(1, searchBean.find(metadata, null, query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("organisasjonsform", "DA");
        assertEquals(15, searchBean.find(metadata, null, query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("organisasjonsform", "AS");
        assertEquals(84, searchBean.find(metadata, null, query, 1).getEntries().size());
    }

    @Test
    public void testElmaParticipants() throws Exception {
        metadata.setLocation("difi/participants");

        fieldBean.update(metadata);
        indexBean.update(metadata);
        searchBean.update(metadata);

        Map<String, String> query;

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "Ja");
        assertEquals(52, searchBean.find(metadata, "oslo kommune", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "\"Ja\"");
        assertEquals(52, searchBean.find(metadata, "oslo kommune", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "J*");
        assertEquals(52, searchBean.find(metadata, "oslo kommune", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "J*");
        assertEquals(52, searchBean.find(metadata, "oslo k*une", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "Ja");
        assertEquals(100, searchBean.find(metadata, "o*", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "Ja");
        assertEquals(100, searchBean.find(metadata, "St*", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("identifier", "991825*");
        assertEquals(2, searchBean.find(metadata, null, query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "Ja");
        assertEquals(0, searchBean.find(metadata, "9908\\:", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "Ja");
        assertEquals(1, searchBean.find(metadata, "time and date", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("identifier", "991825827");
        assertEquals(1, searchBean.find(metadata, null, query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "Ja");
        assertEquals(1, searchBean.find(metadata, "991825827", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "Ja");
        assertEquals(1, searchBean.find(metadata, "\"Time and date\"", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "Ja");
        assertEquals(1, searchBean.find(metadata, "988375713 \"Time and date\"", query, 1).getEntries().size());

        query = new HashMap<String, String>();
        query.put("EHF_INVOICE", "Ja");
        assertEquals(1, searchBean.find(metadata, "988375713 \"Time and date", query, 1).getEntries().size());
    }

    @Test
    public void testDelete() {
        indexBean.delete("difi/miljo/kalkulator");
    }

    @Test
    public void testLookupAdv() throws Exception {
        metadata.setLocation("difi/geo/kommune");
        metadata.setLogger(logger);

        fieldBean.update(metadata);
        indexBean.update(metadata);
        searchBean.update(metadata);

        Map<String, String> query = new HashMap<String, String>();
        query.put("kommune", "1401");
        query.put("fylke", "14");
        assertEquals(1, searchBean.find(metadata, null, query, 1).getEntries().size());
        assertEquals(0, searchBean.find(metadata, null, query, 2).getEntries().size());

        query.clear();
        query.put("kommune", "1401");
        assertEquals(1, searchBean.find(metadata, null, query, 1).getEntries().size());
        assertEquals(0, searchBean.find(metadata, null, query, 2).getEntries().size());

        query.clear();
        query.put("fylke", "14");
        assertEquals(26, searchBean.find(metadata, "", query, 1).getEntries().size());
        assertEquals(0, searchBean.find(metadata, "", query, 2).getEntries().size());

        query.clear();
        query.put("navn", "l*anger");
        assertEquals(2, searchBean.find(metadata, "", query, 1).getEntries().size());
        assertEquals(0, searchBean.find(metadata, "", query, 2).getEntries().size());

        indexBean.delete("difi/geo/kommune");
    }

    @Test
    public void test10000() throws Exception {
        FieldBean fieldBean = Mockito.mock(FieldBean.class);
        indexBean.setFielBean(fieldBean);

        MetadataLogger logger = Mockito.mock(MetadataLogger.class);

        CSVReader parser = Mockito.mock(CSVReader.class);
        Field settingsFactoryField = IndexBean.class.getDeclaredField("csvReaderFactory");
        settingsFactoryField.setAccessible(true);
        settingsFactoryField.set(indexBean, parser);

        Metadata metadata = new Metadata();
        metadata.setLocation("whoknows");
        metadata.setUpdated(10L);
        metadata.setLogger(logger);

        File filename = Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), FILE_DATASET);

        List<FieldLight> fields = new ArrayList<FieldLight>();
        no.difi.datahotel.model.Field field;
        field = new no.difi.datahotel.model.Field();
        field.setShortName("field1");
        field.setSearchable(false);
        fields.add(field.light());

        Map<String, String> line = new HashMap<String, String>();
        line.put("field1", "value");

        when(parser.open(filename)).thenReturn(parser);
        when(fieldBean.getFields(metadata)).thenReturn(fields);
        when(parser.hasNext()).thenReturn(true);
        when(parser.getNextLine()).thenReturn(line);
        doThrow(new RuntimeException()).when(logger).info("Document 20000");

        indexBean.update(metadata);

        verify(parser, times(20000)).getNextLine();

        verify(logger).info("Building index.");
        verify(logger).info("Document 10000");
        verify(logger).info("Document 20000");
    }

    @Test
    @Ignore
    public void testUnableToReadLine() throws Exception {
        FieldBean fieldBean = Mockito.mock(FieldBean.class);

        indexBean.setFielBean(fieldBean);

        MetadataLogger logger = Mockito.mock(MetadataLogger.class);

        CSVReader parser = Mockito.mock(CSVReader.class);
        Field settingsFactoryField = IndexBean.class.getDeclaredField("csvReaderFactory");
        settingsFactoryField.setAccessible(true);
        settingsFactoryField.set(indexBean, parser);

        Metadata metadata = new Metadata();
        metadata.setLocation("whoknows2");
        metadata.setUpdated(10L);
        metadata.setLogger(logger);

        File filename = Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), FILE_DATASET);

        List<FieldLight> fields = new ArrayList<FieldLight>();
        no.difi.datahotel.model.Field field;
        field = new no.difi.datahotel.model.Field();
        field.setShortName("field1");
        field.setSearchable(false);
        fields.add(field.light());

        Map<String, String> line = new HashMap<String, String>();
        line.put("field2", "value");

        when(parser.open(filename)).thenReturn(parser);
        when(fieldBean.getFields(metadata)).thenReturn(fields);
        when(parser.hasNext()).thenReturn(true);
        when(parser.getNextLine()).thenReturn(line);
        doThrow(new RuntimeException()).when(logger).info("Document 10000");
        doThrow(new RuntimeException()).when(logger).info("[NullPointerException][IndexBean.java:88] Unable to index line 1. (null)");

        indexBean.update(metadata);

        verify(parser, times(1)).getNextLine();
        verify(logger).info("Building index.");
    }

    @Test
    public void testUpToDate() {
        MetadataLogger logger = Mockito.mock(MetadataLogger.class);

        Metadata metadata = new Metadata();
        metadata.setLocation("difi/geo/kommune");
        metadata.setUpdated(System.currentTimeMillis());
        metadata.setLogger(logger);

        indexBean.update(metadata);
        searchBean.update(metadata);

        verify(logger).info("Building index.");

        indexBean.update(metadata);
        searchBean.update(metadata);

        verify(logger).info("Index up to date.");
    }
}
