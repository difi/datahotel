package no.difi.datahotel.util.jersey;

public class TextObject implements FormaterInterface {
	
	public String format(Object object, String metadata) {
		return String.valueOf(object);
	}
}