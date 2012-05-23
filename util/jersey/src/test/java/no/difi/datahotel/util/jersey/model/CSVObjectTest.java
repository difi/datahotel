package no.difi.datahotel.util.jersey.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import no.difi.datahotel.util.jersey.CSVData;
import no.difi.datahotel.util.jersey.CSVObject;

import static org.junit.Assert.*;
import org.junit.Test;

public class CSVObjectTest {
	@Test
	public void testCSVData() throws Exception {
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Map<String, String> element;

		element = new HashMap<String, String>();
		element.put("id", "1");
		element.put("name", "Ole");
		data.add(element);

		element = new HashMap<String, String>();
		element.put("id", "2");
		element.put("name", "Per");
		data.add(element);

		CSVObject parser = new CSVObject();
		String result = parser.format(new CSVData(data), null);

		assertEquals(4, result.split(";").length);
		assertEquals(3, result.split("\n").length);
	}

	@Test(expected = Exception.class)
	public void testNull() throws Exception {
		CSVObject parser = new CSVObject();
		parser.format(null, null);
	}
}
