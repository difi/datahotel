package no.difi.datahotel.logic;

import no.difi.datahotel.BaseTest;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.Filesystem;
import no.difi.datahotel.util.MetadataLogger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static no.difi.datahotel.util.Filesystem.FOLDER_CACHE_CHUNK;
import static org.junit.Assert.*;

public class ChunkBeanTest extends BaseTest {

    private ChunkBean chunkBean;

    static MetadataLogger logger;
    static Metadata metadata;

    @Before
    public void before() throws Exception {
        chunkBean = new ChunkBean();
        logger = Mockito.mock(MetadataLogger.class);

        metadata = new Metadata();
        metadata.setUpdated(System.currentTimeMillis());
        metadata.setLogger(logger);
    }

    @Test
    public void testUpdate() {
        metadata.setLocation("difi/test/simple");
        metadata.setShortName("simple");

        chunkBean.update(metadata);

        assertTrue(Filesystem.getFile(FOLDER_CACHE_CHUNK, "difi", "test", "simple", "dataset-1.csv").exists());
        assertTrue(Filesystem.getFile(FOLDER_CACHE_CHUNK, "difi", "test", "simple", "dataset-2.csv").exists());
    }

    @Test
    public void testUpdateError() {
        metadata.setLocation("difi/test/simple-not-here");
        metadata.setShortName("simple-not-here");

        chunkBean.update(metadata);

        // TODO Fikse verifisert bruk av logger
        // Mockito.verify(logger).log(Level.WARNING, null);
    }

    @Test
    public void testGet() throws Exception {
        metadata.setLocation("difi/test/simple");
        metadata.setShortName("simple");

        chunkBean.update(metadata);

        assertEquals(100, chunkBean.get(metadata, 1).getEntries().size());
        assertEquals(19, chunkBean.get(metadata, 2).getEntries().size());

        assertEquals(0, chunkBean.get(metadata, 3).getEntries().size());

        metadata.setLocation("difi/test/simple2");
        assertEquals(0, chunkBean.get(metadata, 1).getEntries().size());

        // Thread.sleep(1000);

        // chunkEJB.delete("difi", "test", "simple2");
    }

    @Test
    public void testOneHundred() throws Exception {
        metadata.setLocation("difi/test/hundred");
        metadata.setShortName("hundred");

        chunkBean.update(metadata);

        assertEquals(100, chunkBean.get(metadata, 1).getEntries().size());
        assertEquals(0, chunkBean.get(metadata, 2).getEntries().size());

        assertEquals(new Long(100), chunkBean.get(metadata, 1).getPosts());

        metadata.setLocation("difi/test/hundred200");
        assertEquals(new Long(0), chunkBean.get(metadata, 1).getPosts());

        // Thread.sleep(1000);

        // chunkEJB.delete("difi", "test", "hundred");
        // assertFalse(Filesystem.getFolderPathF("chunk", "difi", "test",
        // "hundred").exists());
    }

    @Test
    public void testNoNeed() throws Exception {
        metadata.setLocation("difi/test/hundred");
        metadata.setShortName("hundred");

        chunkBean.update(metadata);

        assertEquals(100, chunkBean.get(metadata, 1).getEntries().size());
        assertEquals(0, chunkBean.get(metadata, 2).getEntries().size());
        long ts = Filesystem.getFile(FOLDER_CACHE_CHUNK, metadata.getLocation(), "timestamp").lastModified();

        Thread.sleep(1000);

        chunkBean.update(metadata);
        long ts2 = Filesystem.getFile(FOLDER_CACHE_CHUNK, metadata.getLocation(), "timestamp").lastModified();
        assertEquals(ts, ts2);

        // chunkEJB.delete("difi", "test", "hundred");
        // assertFalse(Filesystem.getFolderPathF("chunk", "difi", "test",
        // "hundred").exists());
    }

    @Test
    public void testReplaceGoal() throws Exception {
        File folder = Filesystem.getFolder(Filesystem.FOLDER_CACHE_CHUNK, "difi/test/hundred", "secret");
        assertTrue(folder.exists());

        metadata.setLocation("difi/test/hundred");
        metadata.setShortName("hundred");

        chunkBean.update(metadata);

        assertFalse(folder.exists());
    }
}
