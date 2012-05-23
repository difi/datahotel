package no.difi.datahotel.util.jersey;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import no.difi.datahotel.util.bridge.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
			builder.append("<error>Unsupported object</error>");
		}
	}
	
	private void parseCSVData(CSVData csvData) {
		try
		{
			if (csvData == null)
				throw new NullPointerException();
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			Element rootElement = doc.createElement("entries");
			doc.appendChild(rootElement);
			
			for(Map<String, String> e : csvData.getEntries()) {
				Element entry = doc.createElement("entry");
				rootElement.appendChild(entry);
				
				for(String key : e.keySet()) {
					Element value = doc.createElement(key);
					entry.appendChild(value);
					value.setTextContent(e.get(key));
				}
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			StringWriter writer = new StringWriter();
			Result result = new StreamResult(writer);
			transformer.transform(source, result);
			builder.append(writer.toString());
			writer.close();
			
		} catch (Exception e) {
			builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			builder.append("<error>An error has occured</error>");
		}
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
	public String format(Object object, String metadata, List<Field> fields) {
		XMLObject x = new XMLObject();
		x.forData(object, metadata);
		return x.getData();
	}
}
