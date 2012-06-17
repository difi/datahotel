package no.difi.datahotel.util.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import no.difi.datahotel.util.shared.Disk;
import no.difi.datahotel.util.shared.Filesystem;

@XmlRootElement(name = "datasetFields")
@XmlAccessorType(XmlAccessType.NONE)
public class Fields {

	@XmlElementWrapper(name = "fields")
	@XmlElement(name = "field")
	private List<Field> fields = new ArrayList<Field>();

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void save(String owner, String group, String dataset) throws Exception {
		Disk.save(Filesystem.getFile(Filesystem.FOLDER_SLAVE, owner, group, dataset, Filesystem.FILE_FIELDS), this);
	}

	public static Fields read(String location) {
		return (Fields) Disk.read(Fields.class,
				Filesystem.getFile(Filesystem.FOLDER_SLAVE, location, Filesystem.FILE_FIELDS));
	}
}
