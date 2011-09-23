package no.difi.datahotel.util.jersey;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing an XML.
 */
public class XMLObject implements FormaterInterface {
	
	private StringBuilder builder = new StringBuilder();
	
	public void forData(Object object, String metadata) {
		
		if(object instanceof CSVData){
			parseCSVData((CSVData)object);
		}
		else if(object instanceof List) {
			parseArrayList((List<?>)object);
		} 
		else {
			builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			builder.append("<error> Unsupported object </error>");
		}
	}
	
	private void parseCSVData(CSVData csvData) {
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		
		if(csvData == null) {
			builder.append("<error>An error has occured</error>");
			return;
		}
		
		builder.append("<entries>\n");
		
		for(Map<String, String> entry : csvData.getEntries()) {
			builder.append("\t<entry>\n");
			
			for(String key : entry.keySet()) {
				builder.append("\t\t<" + key.replaceAll("\\s+", "") + ">");
				builder.append("" + entry.get(key));
				builder.append("</" + key.replaceAll("\\s+", "") + ">\n");
			}
			builder.append("\t</entry>\n");
		}
		
		builder.append("</entries>");
	}
	
	@SuppressWarnings("unchecked")
	private void parseArrayList(List<?> arrayList) {
		
		Class<?> cls = null;
		Object object = null;
		
		if(arrayList.size() > 0) {
			object = arrayList.get(0);
			cls = object.getClass();
		}
		
		if(cls == null || object == null) {
			buildEmptyXml();
			return;
		}
		
		if(cls.isAssignableFrom(String.class))
			parseArrayListWithGenericTypeString((List<String>) arrayList);
		else {
			if(object.getClass().isAnnotationPresent(XmlRootElement.class)) {
				appendHeader();
				parseArrayListWithGenericTypeXMLObject((List<Object>) arrayList);
			}
		}
	}
	
	private void parseArrayListWithGenericTypeString(List<String> list) {
		builder.append("<entry>");
		
		for (Object entry : list) {
			builder.append("<name>");
			builder.append(entry.toString());
			builder.append("</name>");
		}
		
		builder.append("</entry>");
	}
	
	private void parseArrayListWithGenericTypeXMLObject(List<Object> list) {
		if(list.size() == 0) {
			buildEmptyXml();
			return;
		}
		
		Object firstObject = list.get(0);
		String className = firstObject.getClass().getSimpleName().toLowerCase();
		
		builder.append("<" + className + "s>");
		
		for(Object object : list) {
			String metadata = readXmlObjectAsString(object);
			builder.append(metadata.substring(metadata.indexOf('\n')));
		}
		
		builder.append("</" + className + "s>");
	}
	
	private String readXmlObjectAsString(Object object) {

		if (!object.getClass().isAnnotationPresent(XmlRootElement.class))
			return "";

		StringWriter stringWriter = new StringWriter();

		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(object, stringWriter);
		} catch (JAXBException e) {
			return "";
		}
		return stringWriter.toString();
	}
	
	private void appendHeader() {
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	}
	
	private void buildEmptyXml() {
		appendHeader();
	}
	
	public String getData() {
		return builder.toString();
	}

	@Override
	public String format(Object object, String metadata) {
		XMLObject x = new XMLObject();
		x.forData(object, metadata);
		return x.getData();
	}
}
