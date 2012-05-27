package no.difi.datahotel.util.jersey;
import java.util.HashMap;

import no.difi.datahotel.util.jersey.YAMLObject;

import org.junit.Test;

public class YAMLObjectTest {

	public HashMap<String, String> value = new HashMap<String, String>();
	
	@Test
	public void testing()
	{
		value.put("key", "value");
		
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("key", "value");
		values.put("key1", "value1");
		
		YAMLObject yo = new YAMLObject();
		
		System.out.println(yo.format(values, null));
		System.out.println(yo.format(new YAMLObjectTest(), null));
	}
	
}
