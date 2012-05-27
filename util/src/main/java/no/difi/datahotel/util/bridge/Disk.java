package no.difi.datahotel.util.bridge;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

public class Disk {
	public static Object read(Class<?> cls, File path) {
		try {
			JAXBContext jc = JAXBContext.newInstance(cls);
			Unmarshaller u = jc.createUnmarshaller();
			return u.unmarshal(new StreamSource(path), cls).getValue();
		} catch (JAXBException e) {
			Logger.getLogger(cls.getSimpleName()).log(Level.WARNING, "JAXBException", e);
			return null;
		}
	}

	public static void save(File path, Object jaxb) throws Exception {
		JAXBContext context = JAXBContext.newInstance(jaxb.getClass());

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		FileOutputStream out = new FileOutputStream(path);
		m.marshal(jaxb, out);
		out.close();
	}
}
