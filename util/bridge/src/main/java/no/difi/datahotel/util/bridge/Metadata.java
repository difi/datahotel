package no.difi.datahotel.util.bridge;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import no.difi.datahotel.util.shared.Filesystem;

@XmlRootElement
public class Metadata extends Abstract {

	// Values for administration
	private String location;
	private String shortName;
	private Map<String, Metadata> children = new HashMap<String, Metadata>();
	private boolean active;
	private boolean dataset;
	private Long updated;

	// Values for users
	private String name;
	private String description;
	private String url;

	@XmlTransient
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@XmlTransient
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@XmlTransient
	public Map<String, Metadata> getChildren() {
		return children;
	}

	public void setChildren(Map<String, Metadata> children) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getUpdated() {
		return updated;
	}

	public void setUpdated(Long updated) {
		this.updated = updated;
	}

	public void save() throws Exception {
		save(Filesystem.getFileF(Filesystem.FOLDER_SHARED, location, Filesystem.METADATA), this);
	}

	public static Metadata read(String location) {
		File fileMeta = Filesystem.getFileF(Filesystem.FOLDER_SHARED, location, Filesystem.METADATA);
		File fileInactive = Filesystem.getFileF(Filesystem.FOLDER_SHARED, location, Filesystem.INACTIVE);
		File fileDataset = Filesystem.getFileF(Filesystem.FOLDER_SHARED, location, Filesystem.DATASET_DATA);

		Metadata metadata = (Metadata) read(Metadata.class, fileMeta);
		metadata.setLocation(location);
		metadata.setShortName(fileMeta.getName());
		metadata.setActive(fileInactive.exists());
		metadata.setDataset(fileDataset.exists());

		return metadata;
	}
}
