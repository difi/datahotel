package no.difi.datahotel.util.jersey.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;

import no.difi.datahotel.util.jersey.DataFormat;

import org.junit.Test;
import org.svenson.JSON;

public class DataFormatTest {

	@Test
	public void testGet() throws Exception {

		String message = "";

		try {
			DataFormat.get("espen");
		} catch (Exception e) {
			message = e.getMessage();
		}

		assertEquals("MIME not supported.", message);
		assertEquals(DataFormat.JSON, DataFormat.get("json"));
	}

	@Test
	public void testJsonFormat() {
		DataFormat df = DataFormat.JSON;

		ArrayList<String> data = new ArrayList<String>();
		data.add("Espen");
		data.add("Epson");

		assertEquals(JSON.defaultJSON().forValue(data), df.format(data, null));
	}

	@Test
	public void testGetters() {
		DataFormat df = DataFormat.JSON;

		assertEquals("application/json", df.getMime());
		assertEquals("json", df.getType());
	}

	@Test
	public void testError() {
		DataFormat df = DataFormat.JSON;
		String result = df.formatError("WARNING", null);

		assertTrue(result.contains("WARNING"));
		assertTrue(result.contains("error"));
	}

	@Test
	public void testFormatError() throws Exception {
		DataFormat df = DataFormat.JSON;

		Field rField = DataFormat.class.getDeclaredField("cls");
		rField.setAccessible(true);
		rField.set(df, null);

		// TODO Ta over logger.

		assertEquals("Error", df.format("Message", null));
	}

	@Test
	public void testErrorError() throws Exception {
		DataFormat df = DataFormat.JSON;

		Field rField = DataFormat.class.getDeclaredField("cls");
		rField.setAccessible(true);
		rField.set(df, null);

		// TODO Ta over logger.

		assertEquals("Error", df.formatError("Message", null));
	}

}
