package no.difi.datahotel.util.jersey;

/**
 * Class representing an JSONP.
 */
public class JSONPObject extends JSONObject {

	public String format(Object object, String metadata) {
		metadata = metadata == null ? "callback" : metadata;
		return String.valueOf(metadata) + "(" + super.format(object, metadata) + ");";
	}
}