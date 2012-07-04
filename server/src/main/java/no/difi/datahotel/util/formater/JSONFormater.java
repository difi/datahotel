package no.difi.datahotel.util.formater;

import java.util.HashMap;
import java.util.Map;

import no.difi.datahotel.util.FormaterInterface;
import no.difi.datahotel.util.RequestContext;

import com.google.gson.Gson;

/**
 * Class representing JSON.
 */
public class JSONFormater implements FormaterInterface {

	private static Gson gson = new Gson();
	
	@Override
	public String format(Object object, RequestContext context) {
		return gson.toJson(object);
	}

	@Override
	public String format(Exception exception, RequestContext context) {
		Map<String, String> object = new HashMap<String, String>();
		// object.put("status", String.valueOf(exception.getStatus()));
		object.put("error", exception.getMessage());

		return format(object, context);
	}
}