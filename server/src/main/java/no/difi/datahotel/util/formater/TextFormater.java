package no.difi.datahotel.util.formater;

import no.difi.datahotel.util.FormaterInterface;
import no.difi.datahotel.util.RequestContext;


public class TextFormater implements FormaterInterface {
	
	public String format(Object object, RequestContext context) {
		return String.valueOf(object);
	}
}