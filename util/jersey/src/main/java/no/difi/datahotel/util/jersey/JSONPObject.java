package no.difi.datahotel.util.jersey;

import java.util.List;

import no.difi.datahotel.util.bridge.Field;

/**
 * Class representing an JSONP.
 */
public class JSONPObject extends JSONObject {

	public String format(Object object, String metadata, List<Field> fields) {
		metadata = metadata == null ? "callback" : metadata;
		return String.valueOf(metadata) + "(" + super.format(object, metadata, fields) + ");";
	}
}