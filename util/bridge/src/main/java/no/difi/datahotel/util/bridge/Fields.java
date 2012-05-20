package no.difi.datahotel.util.bridge;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import no.difi.datahotel.util.shared.Filesystem;

@XmlRootElement(name = "datasetFields")
@XmlAccessorType(XmlAccessType.NONE)
public class Fields extends Abstract {

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
		save(Filesystem.getFileF(Filesystem.FOLDER_SHARED, owner, group, dataset, Filesystem.DATASET_FIELDS), this);
	}

	public static Fields read(String location) {
		return (Fields) read(Fields.class,
				Filesystem.getFileF(Filesystem.FOLDER_SHARED, location, Filesystem.DATASET_FIELDS));
	}
}
