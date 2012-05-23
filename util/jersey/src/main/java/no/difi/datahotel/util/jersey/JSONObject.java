package no.difi.datahotel.util.jersey;

import org.svenson.JSON;

/**
 * Class representing JSON.
 */
public class JSONObject implements FormaterInterface {

	public String format(Object object, RequestContext context) {
		return JSON.defaultJSON().forValue(object);
	}
}