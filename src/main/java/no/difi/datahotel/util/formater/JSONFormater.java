package no.difi.datahotel.util.formater;

import no.difi.datahotel.util.FormaterInterface;
import no.difi.datahotel.util.RequestContext;

import com.google.gson.Gson;

/**
 * Class representing JSON.
 */
public class JSONFormater implements FormaterInterface {

	private static Gson gson;

	public JSONFormater() {
		gson = new Gson();
	}

	@Override
	public String format(Object object, RequestContext context) {
		return gson.toJson(object);
	}
}