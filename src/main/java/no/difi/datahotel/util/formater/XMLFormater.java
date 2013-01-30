package no.difi.datahotel.util.formater;

import java.util.AbstractMap;
import java.util.Map;

import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.MetadataLight;
import no.difi.datahotel.model.Result;
import no.difi.datahotel.util.FormaterInterface;
import no.difi.datahotel.util.RequestContext;
import no.difi.datahotel.util.SimpleError;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

/**
 * Class representing an XML.
 */
public class XMLFormater implements FormaterInterface {

	private XStream xstream;

	public XMLFormater() {
		XmlFriendlyNameCoder replacer = new XmlFriendlyNameCoder("dd", "_");
		
		xstream = new XStream(new DomDriver("UTF-8", replacer));

		xstream.alias("metadata", MetadataLight.class);
		xstream.alias("result", Result.class);
		xstream.alias("entry", Map.class);
		xstream.alias("field", FieldLight.class);
		xstream.alias("error", SimpleError.class);

		xstream.useAttributeFor("page", Long.class);
		xstream.useAttributeFor("pages", Long.class);
		xstream.useAttributeFor("posts", Long.class);

		xstream.registerConverter(new MapConverter());
	}

	@Override
	public String format(Object object, RequestContext context) {
		return xstream.toXML(object);
	}

	static class MapConverter implements Converter {

		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return AbstractMap.class.isAssignableFrom(clazz);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Map<String, String> map = (AbstractMap) value;
			for (String key : map.keySet()) {
				writer.startNode(key);
				writer.setValue(map.get(key));
				writer.endNode();
			}
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return null;
		}
	}
}
