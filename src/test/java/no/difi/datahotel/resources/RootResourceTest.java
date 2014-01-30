package no.difi.datahotel.resources;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class RootResourceTest {

    private RootResource rootResource;

    @Before
    public void before() {
        rootResource = new RootResource();
    }

    @Test
    public void testGetBrowse() {
        Response response = rootResource.getBrowse();

        Assert.assertNull(response.getEntity());
        Assert.assertEquals(301, response.getStatus());
    }

    @Test
    public void testGetDefinitions() {
        Response response = rootResource.getDefinitions();

        Assert.assertNull(response.getEntity());
        Assert.assertEquals(301, response.getStatus());
    }

    @Test
    public void testGetFrontpage() {
        Response response = rootResource.getFrontpage();

        Assert.assertEquals("Placeholder", response.getEntity());
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetApi() {
        Response response = rootResource.getApi();

        Assert.assertEquals("Placeholder", response.getEntity());
        Assert.assertEquals(200, response.getStatus());
    }
}
