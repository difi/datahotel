package no.difi.datahotel.resources;

import com.sun.jersey.api.core.HttpRequestContext;
import no.difi.datahotel.logic.ChunkBean;
import no.difi.datahotel.logic.DataBean;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.DatahotelException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.lang.reflect.Field;

public class DownloadResourceTest {

    private DataBean dataBean;
    private ChunkBean chunkBean;
    private DownloadResource downloadResource;

    private UriInfo uriInfo;
    private HttpRequestContext httpRequestContext;

    private Metadata metadata;

    @Before
    public void before() throws Exception {
        metadata = new Metadata();
        metadata.setLocation("dataset");

        dataBean = Mockito.mock(DataBean.class);
        Mockito.when(dataBean.getChild(metadata.getLocation())).thenReturn(metadata);

        chunkBean = Mockito.mock(ChunkBean.class);

        uriInfo = Mockito.mock(UriInfo.class);

        httpRequestContext = Mockito.mock(HttpRequestContext.class);
        Mockito.when(httpRequestContext.getHeaderValue(HttpHeaders.IF_NONE_MATCH)).thenReturn(null);

        downloadResource = new DownloadResource();
        downloadResource.setDataEJB(dataBean);
        downloadResource.setChunkEJB(chunkBean);

        Field uriInfoField = BaseResource.class.getDeclaredField("uriInfo");
        uriInfoField.setAccessible(true);
        uriInfoField.set(downloadResource, uriInfo);

        Field requestField = BaseResource.class.getDeclaredField("request");
        requestField.setAccessible(true);
        requestField.set(downloadResource, httpRequestContext);
    }

    @Test
    public void testGetFullDataset() {
        File file = new File("/some/path");

        Mockito.when(chunkBean.getFullDataset(metadata)).thenReturn(file);

        Response response = downloadResource.getFullDataset(metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(file, response.getEntity());
    }

    @Test
    public void testGetFullDatasetException() {
        Mockito.when(chunkBean.getFullDataset(metadata)).thenThrow(new RuntimeException("Catch me!"));

        DatahotelException ex = null;
        try {
            downloadResource.getFullDataset(metadata.getLocation());
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(500, ex.getStatus());
        Assert.assertEquals("Catch me!", ex.getMessage());
    }


    @Test
    public void testGetMetadata() {
        File file = new File("/some/path/meta.xml");

        Mockito.when(chunkBean.getMetadata(metadata)).thenReturn(file);

        Response response = downloadResource.getMetadata(metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(file, response.getEntity());
    }

    @Test
    public void testGetMetadataException() {
        Mockito.when(chunkBean.getMetadata(metadata)).thenThrow(new RuntimeException("Catch me!"));

        DatahotelException ex = null;
        try {
            downloadResource.getMetadata(metadata.getLocation());
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(500, ex.getStatus());
        Assert.assertEquals("Catch me!", ex.getMessage());
    }

    @Test
    public void testGetFields() {
        File file = new File("/some/path/fields.xml");

        Mockito.when(chunkBean.getFields(metadata)).thenReturn(file);

        Response response = downloadResource.getFields(metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(file, response.getEntity());
    }

    @Test
    public void testGetFieldsException() {
        Mockito.when(chunkBean.getFields(metadata)).thenThrow(new RuntimeException("Catch me!"));

        DatahotelException ex = null;
        try {
            downloadResource.getFields(metadata.getLocation());
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(500, ex.getStatus());
        Assert.assertEquals("Catch me!", ex.getMessage());
    }

}
