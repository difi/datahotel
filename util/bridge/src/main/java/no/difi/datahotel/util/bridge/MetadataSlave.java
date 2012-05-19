package no.difi.datahotel.util.bridge;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import no.difi.datahotel.util.shared.Filesystem;

@XmlRootElement(name = "metadata")
public class MetadataSlave extends Metadata {

	private String location;
	private Map<String, MetadataSlave> children = new HashMap<String, MetadataSlave>();
	private boolean active;
	private boolean dataset;

	@XmlTransient
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@XmlTransient
	public Map<String, MetadataSlave> getChildren() {
		return children;
	}

	public void setChildren(Map<String, MetadataSlave> children) {
		this.children = children;
	}

	@XmlTransient
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@XmlTransient
	public boolean isDataset() {
		return dataset;
	}

	public void setDataset(boolean dataset) {
		this.dataset = dataset;
	}

	public void save() throws Exception {
		save(Filesystem.getFileF(Filesystem.FOLDER_SHARED, location, Filesystem.METADATA), this);
	}

}
