package no.difi.datahotel.slave.resources;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import no.difi.datahotel.BaseTest;
import no.difi.datahotel.model.Definition;
import no.difi.datahotel.model.DefinitionLight;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.slave.logic.FieldBean;
import no.difi.datahotel.util.DatahotelException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.core.HttpRequestContext;

public class DefinitionResourceTest extends BaseTest {

	private FieldBean fieldBean;

	private DefinitionResource definitionResource;

	private UriInfo uriInfo;
	private HttpRequestContext httpRequestContext;
	
	private MultivaluedMap<String, String> parameterMap;

	@SuppressWarnings("unchecked")
	@Before
	public void before() throws Exception {
		fieldBean = Mockito.mock(FieldBean.class);

		parameterMap = Mockito.mock(MultivaluedMap.class);
		Mockito.when(parameterMap.containsKey("query")).thenReturn(false);
		Mockito.when(parameterMap.containsKey("callback")).thenReturn(false);
		Mockito.when(parameterMap.containsKey("page")).thenReturn(false);
		
		uriInfo = Mockito.mock(UriInfo.class);
		Mockito.when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		httpRequestContext = Mockito.mock(HttpRequestContext.class);
		Mockito.when(httpRequestContext.getHeaderValue(HttpHeaders.IF_NONE_MATCH)).thenReturn(null);

		definitionResource = new DefinitionResource();
		definitionResource.setFieldEJB(fieldBean);

		Field uriInfoField = BaseResource.class.getDeclaredField("uriInfo");
		uriInfoField.setAccessible(true);
		uriInfoField.set(definitionResource, uriInfo);

		Field requestField = BaseResource.class.getDeclaredField("request");
		requestField.setAccessible(true);
		requestField.set(definitionResource, httpRequestContext);

	}

	@Test
	public void testGetDefinitions() {
		List<DefinitionLight> defs = new ArrayList<DefinitionLight>();
		defs.add(new DefinitionLight(new Definition()));
		
		Mockito.when(fieldBean.getDefinitions()).thenReturn(defs);
		
		Response response = definitionResource.getDefinitions("xml");

		Assert.assertEquals(200, response.getStatus());
	}

	@Test
	public void testGetDefinitionsEmpty() {
		Mockito.when(fieldBean.getDefinitions()).thenReturn(new ArrayList<DefinitionLight>());

		DatahotelException ex = null;
		try {
			definitionResource.getDefinitions("xml");
			Assert.fail();
		} catch (DatahotelException e) {
			ex = e;
		}

		Assert.assertNotNull(ex);
		Assert.assertEquals(404, ex.getStatus());
		Assert.assertEquals("No definitions available.", ex.getMessage());
	}
	
	@Test
	public void testGetDefinitionsException() {
		Mockito.when(fieldBean.getDefinitions()).thenThrow(new RuntimeException("Catch me!"));

		DatahotelException ex = null;
		try {
			definitionResource.getDefinitions("xml");
			Assert.fail();
		} catch (DatahotelException e) {
			ex = e;
		}

		Assert.assertNotNull(ex);
		Assert.assertEquals(500, ex.getStatus());
		Assert.assertEquals("Catch me!", ex.getMessage());
	}

	@Test
	public void testGetDefinitionSimple() {
		Metadata metadata = new Metadata();
		metadata.setLocation("dataset");
		
		no.difi.datahotel.model.Field field1 = new no.difi.datahotel.model.Field();
		field1.setMetadata(metadata);
		
		List<no.difi.datahotel.model.Field> fields = new ArrayList<no.difi.datahotel.model.Field>();
		fields.add(field1);
		
		Mockito.when(fieldBean.getUsage("def1")).thenReturn(fields);

		Response response = definitionResource.getDefinition("xml", "def1");

		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity().toString().contains("dataset"));
	}

	@Test
	public void testGetDefinitionDuplicate() {
		Metadata metadata = new Metadata();
		metadata.setLocation("dataset");
		
		no.difi.datahotel.model.Field field1 = new no.difi.datahotel.model.Field();
		field1.setMetadata(metadata);
		
		no.difi.datahotel.model.Field field2 = new no.difi.datahotel.model.Field();
		field2.setMetadata(metadata);

		List<no.difi.datahotel.model.Field> fields = new ArrayList<no.difi.datahotel.model.Field>();
		fields.add(field1);
		fields.add(field2);
		
		Mockito.when(fieldBean.getUsage("def1")).thenReturn(fields);

		Response response = definitionResource.getDefinition("xml", "def1");

		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertFalse(response.getEntity().toString().replaceFirst("dataset", "").contains("dataset"));
	}

	@Test
	public void testGetDefinitionEmpty() {
		Mockito.when(fieldBean.getUsage("field1")).thenReturn(new ArrayList<no.difi.datahotel.model.Field>());

		DatahotelException ex = null;
		try {
			definitionResource.getDefinition("xml", "field1");
			Assert.fail();
		} catch (DatahotelException e) {
			ex = e;
		}

		Assert.assertNotNull(ex);
		Assert.assertEquals(404, ex.getStatus());
		Assert.assertEquals("Definition never used.", ex.getMessage());
	}

	@Test
	public void testGetDefinitionException() {
		Mockito.when(fieldBean.getUsage("field1")).thenThrow(new RuntimeException("Catch me!"));

		DatahotelException ex = null;
		try {
			definitionResource.getDefinition("xml", "field1");
			Assert.fail();
		} catch (DatahotelException e) {
			ex = e;
		}

		Assert.assertNotNull(ex);
		Assert.assertEquals(500, ex.getStatus());
		Assert.assertEquals("Catch me!", ex.getMessage());
	}
}
