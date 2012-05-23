package no.difi.datahotel.util.jersey;


/**
 * Class representing an JSONP.
 */
public class JSONPObject extends JSONObject {

	public String format(Object object, RequestContext context) {
		String callback = context.getCallback() == null ? "callback" : context.getCallback();
		return String.valueOf(callback) + "(" + super.format(object, context) + ");";
	}
}