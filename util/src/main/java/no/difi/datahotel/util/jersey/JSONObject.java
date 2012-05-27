package no.difi.datahotel.util.jersey;

import com.google.gson.Gson;

/**
 * Class representing JSON.
 */
public class JSONObject implements FormaterInterface {

	public String format(Object object, RequestContext context) {
		return new Gson().toJson(object);
	}
}