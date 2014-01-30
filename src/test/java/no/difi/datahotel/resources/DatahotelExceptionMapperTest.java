package no.difi.datahotel.resources;

import no.difi.datahotel.BaseTest;
import no.difi.datahotel.util.DatahotelException;
import no.difi.datahotel.util.Formater;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class DatahotelExceptionMapperTest extends BaseTest {

    private DatahotelExceptionMapper mapper = new DatahotelExceptionMapper();

    @Test
    public void testNotModified() {
        Response response = mapper.toResponse(new DatahotelException(304, "Not modified").setFormater(Formater.XML));

        Assert.assertNotNull(response);
        Assert.assertNull(response.getEntity());
        Assert.assertEquals(304, response.getStatus());
    }

    @Test
    public void testNotFound() {
        Response response = mapper.toResponse(new DatahotelException(404, "File not found").setFormater(Formater.XML));

        Assert.assertNotNull(response);
        Assert.assertTrue(response.getEntity().toString().contains("File not found"));
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testServerError() {
        Response response = mapper.toResponse(new DatahotelException(500, "Whoot?").setFormater(Formater.XML));

        Assert.assertNotNull(response);
        Assert.assertTrue(response.getEntity().toString().contains("Whoot?"));
        Assert.assertEquals(500, response.getStatus());
    }
}
