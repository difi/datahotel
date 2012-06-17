package no.difi.datahotel.util.formater;

import no.difi.datahotel.util.RequestContext;


/**
 * Class representing an JSONP.
 */
public class JSONPFormater extends JSONFormater {

	public String format(Object object, RequestContext context) {
		String callback = context.getCallback() == null ? "callback" : context.getCallback();
		return String.valueOf(callback) + "(" + super.format(object, context) + ");";
	}
}