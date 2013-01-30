package no.difi.datahotel.util.formater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import no.difi.datahotel.BaseTest;
import no.difi.datahotel.model.Result;
import no.difi.datahotel.util.formater.CSVFormater;

import static org.junit.Assert.*;
import org.junit.Test;

public class CSVFormaterTest extends BaseTest {
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

		CSVFormater parser = new CSVFormater();
		String result = parser.format(new Result(data), null);

		assertEquals(4, result.split(";").length);
		assertEquals(3, result.split("\n").length);
	}

	@Test(expected = Exception.class)
	public void testNull() throws Exception {
		CSVFormater parser = new CSVFormater();
		parser.format(null, null);
	}
}
