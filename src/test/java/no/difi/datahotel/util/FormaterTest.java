package no.difi.datahotel.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import no.difi.datahotel.BaseTest;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.MetadataLight;
import no.difi.datahotel.model.Result;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;

public class FormaterTest extends BaseTest {

	@Test
	public void testGet() throws Exception {
		assertEquals(Formater.JSON, Formater.get("json"));

		DatahotelException exeption = null;
		try {
			Formater.get("espen");
		} catch (DatahotelException e) {
			exeption = e;
		}
		assertNotNull(exeption);

	}

	@Test
	public void testJsonFormat() {
		Formater df = Formater.JSON;

		ArrayList<String> data = new ArrayList<String>();
		data.add("Espen");
		data.add("Epson");

		assertEquals(new Gson().toJson(data), df.format(data, null));
	}

	@Test
	public void testGetters() {
		Formater df = Formater.JSON;

		assertEquals("application/json;charset=UTF-8", df.getMime());
		assertEquals("json", df.getType());
	}

	@Test
	public void testError() {
		Formater df = Formater.JSON;
		String result = df.formatError(new Exception("WARNING"), null);

		assertTrue(result.contains("WARNING"));
		assertTrue(result.contains("message"));
	}

	@Test
	public void testFormatError() throws Exception {
		Formater df = Formater.JSON;

		Field rField = Formater.class.getDeclaredField("cls");
		rField.setAccessible(true);
		rField.set(df, null);

		rField = Formater.class.getDeclaredField("logger");
		rField.setAccessible(true);
		rField.set(df, Mockito.mock(Logger.class));

		assertEquals("Error", df.format("Message", null));
	}

	@Test
	public void testErrorError() throws Exception {
		Formater df = Formater.JSON;

		Field rField = Formater.class.getDeclaredField("cls");
		rField.setAccessible(true);
		rField.set(df, null);

		rField = Formater.class.getDeclaredField("logger");
		rField.setAccessible(true);
		rField.set(df, Mockito.mock(Logger.class));

		assertEquals("Error", df.formatError(new Exception("Message"), null));
	}

	/**
	 * Fast and simple testing of most types.
	 * 
	 * TODO Fix JSON and CSV.
	 */
	@Test
	public void testSimple() {
		List<Map<String, String>> d = new ArrayList<Map<String, String>>();
		Result data = new Result(d);
		HashMap<String, String> ds = new HashMap<String, String>();
		ds.put("description", "Values");
		ds.put("name", "Hello World!");
		d.add(ds);

		List<MetadataLight> metadata = new ArrayList<MetadataLight>();
		Metadata mp = new Metadata();
		mp.setName("Difi");
		mp.setDescription("Agency");
		mp.setUrl("http://www.difi.no/");
		mp.setUpdated(System.currentTimeMillis());

		MetadataLight m = mp.light();
		metadata.add(m);

		RequestContext context = new RequestContext();

		for (Formater f : Formater.values()) {
			if (f != Formater.JSON) {
				assertTrue(f.format(data, context).contains("World"));
				assertTrue(f.format(data, context).contains("escription"));
			}

			if (f != Formater.JSON && f != Formater.CSV && f != Formater.CSVCORRECT) {
				assertTrue(f.format(metadata, context).contains("Difi"));
				assertTrue(f.format(metadata, context).contains("escription"));
				assertTrue(f.format(metadata, context).contains("www.difi.no"));
			}
		}
	}

}
