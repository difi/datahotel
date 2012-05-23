package no.difi.datahotel.util.jersey;

import java.util.List;

import no.difi.datahotel.util.bridge.Field;

import org.svenson.JSON;

/**
 * Class representing JSON.
 */
public class JSONObject implements FormaterInterface {

	public String format(Object object, String metadata, List<Field> fields) {
		return JSON.defaultJSON().forValue(object);
	}
}