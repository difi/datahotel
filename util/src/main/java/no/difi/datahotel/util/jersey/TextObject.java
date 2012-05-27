package no.difi.datahotel.util.jersey;


public class TextObject implements FormaterInterface {
	
	public String format(Object object, RequestContext context) {
		return String.valueOf(object);
	}
}