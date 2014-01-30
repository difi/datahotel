package no.difi.datahotel.resources;

import com.sun.jersey.api.core.HttpRequestContext;
import no.difi.datahotel.BaseTest;
import no.difi.datahotel.logic.ChunkBean;
import no.difi.datahotel.logic.DataBean;
import no.difi.datahotel.logic.FieldBean;
import no.difi.datahotel.logic.SearchBean;
import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.MetadataLight;
import no.difi.datahotel.model.Result;
import no.difi.datahotel.util.DatahotelException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BrowseResourceTest extends BaseTest {

    private DataBean dataBean;
    private FieldBean fieldBean;
    private ChunkBean chunkBean;
    private SearchBean searchBean;
    private BrowseResource browseResource;

    private UriInfo uriInfo;
    private HttpRequestContext httpRequestContext;

    private MultivaluedMap<String, String> parameterMap;

    private Metadata metadata;
    private no.difi.datahotel.model.Field field1;
    private no.difi.datahotel.model.Field field2;
    private ArrayList<FieldLight> fields;
    private Result result;


    @SuppressWarnings("unchecked")
    @Before
    public void before() throws Exception {
        metadata = new Metadata();
        metadata.setLocation("dataset");

        field1 = new no.difi.datahotel.model.Field("field1", false);

        field2 = new no.difi.datahotel.model.Field("field2", true);
        field2.setGroupable(true);

        fields = new ArrayList<FieldLight>();
        fields.add(new FieldLight(field1));
        fields.add(new FieldLight(field2));

        result = new Result();

        dataBean = Mockito.mock(DataBean.class);
        Mockito.when(dataBean.getChild("")).thenReturn(metadata);
        Mockito.when(dataBean.getChild(metadata.getLocation())).thenReturn(metadata);

        fieldBean = Mockito.mock(FieldBean.class);

        chunkBean = Mockito.mock(ChunkBean.class);

        searchBean = Mockito.mock(SearchBean.class);

        parameterMap = Mockito.mock(MultivaluedMap.class);
        Mockito.when(parameterMap.containsKey("query")).thenReturn(false);
        Mockito.when(parameterMap.containsKey("callback")).thenReturn(false);
        Mockito.when(parameterMap.containsKey("page")).thenReturn(false);

        uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

        httpRequestContext = Mockito.mock(HttpRequestContext.class);
        Mockito.when(httpRequestContext.getHeaderValue(HttpHeaders.IF_NONE_MATCH)).thenReturn(null);

        browseResource = new BrowseResource();
        browseResource.setDataEJB(dataBean);
        browseResource.setFieldEJB(fieldBean);
        browseResource.setChunkEJB(chunkBean);
        browseResource.setSearchEJB(searchBean);

        Field uriInfoField = BaseResource.class.getDeclaredField("uriInfo");
        uriInfoField.setAccessible(true);
        uriInfoField.set(browseResource, uriInfo);

        Field requestField = BaseResource.class.getDeclaredField("request");
        requestField.setAccessible(true);
        requestField.set(browseResource, httpRequestContext);
    }

    @Test
    public void testGetAllDatasetsSimple() {
        ArrayList<Metadata> metadatas = new ArrayList<Metadata>();
        metadatas.add(new Metadata());
        metadatas.add(new Metadata());

        Mockito.when(dataBean.getDatasets()).thenReturn(metadatas);

        Response response = browseResource.getAllDatasets("xml");

        Assert.assertNotNull(response);
    }

    @Test
    public void testGetAllDatasetsNoResult() {
        Mockito.when(dataBean.getDatasets()).thenReturn(new ArrayList<Metadata>());

        DatahotelException ex = null;
        try {
            browseResource.getAllDatasets("xml");
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(404, ex.getStatus());
        Assert.assertEquals("No elements found.", ex.getMessage());
    }

    @Test
    public void testGetAllDatasetsInactiveParent() {
        ArrayList<Metadata> metadatas = new ArrayList<Metadata>();

        Metadata toBeActiveParent = new Metadata();
        toBeActiveParent.setActive(true);

        Metadata toBeActive = new Metadata();
        toBeActive.setParent(toBeActiveParent);
        toBeActive.setLocation("tobeactive");

        Metadata toBeInactiveParent = new Metadata();
        toBeInactiveParent.setActive(false);

        Metadata toBeInactive = new Metadata();
        toBeInactive.setParent(toBeInactiveParent);
        toBeInactive.setLocation("tobeinactive");

        metadatas.add(toBeActive);
        metadatas.add(toBeInactive);

        Mockito.when(dataBean.getDatasets()).thenReturn(metadatas);

        Response response = browseResource.getAllDatasets("xml");

        Assert.assertNotNull(response);
        Assert.assertTrue(response.getEntity().toString().contains("tobeactive"));
        Assert.assertFalse(response.getEntity().toString().contains("tobeinactive"));
    }

    @Test
    public void testGetAllDatasetsException() {
        Mockito.when(dataBean.getDatasets()).thenThrow(new RuntimeException("Catch me!"));

        DatahotelException ex = null;
        try {
            browseResource.getAllDatasets("xml");
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(500, ex.getStatus());
        Assert.assertEquals("Catch me!", ex.getMessage());
    }

    @Test
    public void testGetFieldsSimple() {
        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);

        Response response = browseResource.getFields("xml", metadata.getLocation());

        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertTrue(response.getEntity().toString().contains("field1"));
        Assert.assertTrue(response.getEntity().toString().contains("field2"));
        Assert.assertTrue(response.getEntity().toString().contains("searchable"));
    }

    @Test
    public void testGetFieldsEmpty() {
        Mockito.when(fieldBean.getFields(metadata)).thenReturn(new ArrayList<FieldLight>());

        DatahotelException ex = null;
        try {
            browseResource.getFields("xml", metadata.getLocation());
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(404, ex.getStatus());
        Assert.assertEquals("Metadata with that name could not be found.", ex.getMessage());
    }

    @Test
    public void testGetFieldsException() {
        Mockito.when(fieldBean.getFields(metadata)).thenThrow(new RuntimeException("Catch me!"));

        DatahotelException ex = null;
        try {
            browseResource.getFields("xml", metadata.getLocation());
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(500, ex.getStatus());
        Assert.assertEquals("Catch me!", ex.getMessage());
    }

    @Test
    public void testGetMetaSimple() {
        Response response = browseResource.getMeta("xml", metadata.getLocation());

        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertTrue(response.getEntity().toString().contains("dataset"));
        Assert.assertTrue(response.getEntity().toString().contains("false"));
    }

    @Test
    public void testGetMetaCallback() {
        Mockito.when(parameterMap.containsKey("callback")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("callback")).thenReturn("my_function");

        Response response = browseResource.getMeta("jsonp", metadata.getLocation());

        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertTrue(response.getEntity().toString().contains("dataset"));
        Assert.assertTrue(response.getEntity().toString().contains("false"));
        Assert.assertTrue(response.getEntity().toString().contains("my_function"));
    }

    @Test
    public void testGetMetaEmptyCallback() {
        Mockito.when(parameterMap.containsKey("callback")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("callback")).thenReturn("");

        Response response = browseResource.getMeta("jsonp", metadata.getLocation());

        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertTrue(response.getEntity().toString().contains("dataset"));
        Assert.assertTrue(response.getEntity().toString().contains("false"));
        Assert.assertTrue(response.getEntity().toString().contains("callback"));
    }

    @Test
    public void testGetMetaModified() {
        Mockito.when(httpRequestContext.getHeaderValue(HttpHeaders.IF_NONE_MATCH)).thenReturn("123456789");

        Response response = browseResource.getMeta("xml", metadata.getLocation());

        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertTrue(response.getEntity().toString().contains("dataset"));
        Assert.assertTrue(response.getEntity().toString().contains("false"));
    }

    @Test
    public void testGetMetaNotModified() {
        Mockito.when(httpRequestContext.getHeaderValue(HttpHeaders.IF_NONE_MATCH)).thenReturn(String.valueOf(metadata.getUpdated()));

        DatahotelException ex = null;
        try {
            browseResource.getMeta("xml", metadata.getLocation());
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(304, ex.getStatus());
        Assert.assertEquals("Not modified", ex.getMessage());
    }

    @Test
    public void testGetMetaException() {
        Mockito.when(dataBean.getChild(metadata.getLocation())).thenThrow(new RuntimeException("Catch me!"));

        DatahotelException ex = null;
        try {
            browseResource.getMeta("xml", metadata.getLocation());
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(500, ex.getStatus());
        Assert.assertEquals("Catch me!", ex.getMessage());
    }

    @Test
    public void testGetRootSimple() {
        Mockito.when(dataBean.getChildren("")).thenReturn(new ArrayList<MetadataLight>());

        Response response = browseResource.getRoot("xml");

        Assert.assertEquals(200, response.getStatus());

    }

    @Test
    public void testGetRootNoChildren() {
        Mockito.when(dataBean.getChildren("")).thenReturn(null);

        DatahotelException ex = null;
        try {
            browseResource.getRoot("xml");
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(404, ex.getStatus());
        Assert.assertEquals("No elements found.", ex.getMessage());
    }

    @Test
    public void testGetDatasetException() {
        Mockito.when(dataBean.getChild(metadata.getLocation())).thenThrow(new RuntimeException("Catch me!"));

        DatahotelException ex = null;
        try {
            browseResource.getDataset("xml", metadata.getLocation());
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(500, ex.getStatus());
        Assert.assertEquals("Catch me!", ex.getMessage());
    }

    @Test
    public void testGetDatasetSimple() {
        metadata.setDataset(true);

        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);
        Mockito.when(chunkBean.get(metadata, 1)).thenReturn(result);

        Response response = browseResource.getDataset("xml", metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDatasetNoResult() {
        metadata.setDataset(true);

        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);
        Mockito.when(chunkBean.get(metadata, 1)).thenReturn(null);

        DatahotelException ex = null;
        try {
            browseResource.getDataset("xml", metadata.getLocation());
            Assert.fail();
        } catch (DatahotelException e) {
            ex = e;
        }

        Assert.assertNotNull(ex);
        Assert.assertEquals(500, ex.getStatus());
        Assert.assertEquals("No data retrieved.", ex.getMessage());
    }

    @Test
    public void testGetDatasetPageEmpty() {
        metadata.setDataset(true);

        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);
        Mockito.when(chunkBean.get(metadata, 1)).thenReturn(result);
        Mockito.when(parameterMap.containsKey("page")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("page")).thenReturn("");

        Response response = browseResource.getDataset("xml", metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDatasetPageTwo() {
        metadata.setDataset(true);

        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);
        Mockito.when(chunkBean.get(metadata, 2)).thenReturn(result);
        Mockito.when(parameterMap.containsKey("page")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("page")).thenReturn("2");

        Response response = browseResource.getDataset("xml", metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDatasetQuery() {
        metadata.setDataset(true);

        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);
        Mockito.when(searchBean.find(metadata, "myquery", new HashMap<String, String>(), 1)).thenReturn(result);
        Mockito.when(parameterMap.containsKey("query")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("query")).thenReturn("myquery");

        Response response = browseResource.getDataset("xml", metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDatasetQueryEmpty() {
        metadata.setDataset(true);

        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);
        Mockito.when(chunkBean.get(metadata, 1)).thenReturn(result);
        Mockito.when(parameterMap.containsKey("query")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("query")).thenReturn("");

        Response response = browseResource.getDataset("xml", metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDatasetLookup() {
        metadata.setDataset(true);

        Map<String, String> lookup = new HashMap<String, String>();
        lookup.put("field2", "myvalue");

        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);
        Mockito.when(searchBean.find(metadata, null, lookup, 1)).thenReturn(result);
        Mockito.when(parameterMap.containsKey("field2")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("field2")).thenReturn("myvalue");

        Response response = browseResource.getDataset("xml", metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDatasetLookupEmpty() {
        metadata.setDataset(true);

        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);
        Mockito.when(chunkBean.get(metadata, 1)).thenReturn(result);
        Mockito.when(parameterMap.containsKey("field2")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("field2")).thenReturn("");

        Response response = browseResource.getDataset("xml", metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDatasetLookupAndQuery() {
        metadata.setDataset(true);

        Map<String, String> lookup = new HashMap<String, String>();
        lookup.put("field2", "myvalue");

        Mockito.when(fieldBean.getFields(metadata)).thenReturn(fields);
        Mockito.when(searchBean.find(metadata, "myquery", lookup, 1)).thenReturn(result);
        Mockito.when(parameterMap.containsKey("field2")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("field2")).thenReturn("myvalue");
        Mockito.when(parameterMap.containsKey("query")).thenReturn(true);
        Mockito.when(parameterMap.getFirst("query")).thenReturn("myquery");

        Response response = browseResource.getDataset("xml", metadata.getLocation());

        Assert.assertEquals(200, response.getStatus());
    }
}
