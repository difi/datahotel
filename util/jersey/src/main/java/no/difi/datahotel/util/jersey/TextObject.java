package no.difi.datahotel.util.jersey;

import java.util.List;

import no.difi.datahotel.util.bridge.Field;

public class TextObject implements FormaterInterface {
	
	public String format(Object object, String metadata, List<Field> fields) {
		return String.valueOf(object);
	}
}