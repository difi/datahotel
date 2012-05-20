package no.difi.datahotel.util.bridge;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import no.difi.datahotel.util.shared.Filesystem;

@XmlRootElement
public class Metadata extends Abstract {

	private String location;
	private Map<String, Metadata> children = new HashMap<String, Metadata>();
	private boolean active;
	private boolean dataset;

	// Values for users
	private String shortName;
	private String name;
	private String description;
	private String url;
	private Long updated;

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
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

	@XmlTransient
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@XmlTransient
	public Map<String, Metadata> getChildren() {
		return children;
	}

	public void addChild(Metadata child) {
		this.children.put(child.getShortName(), child);

		if (child.isActive() && child.updated != null)
			if (this.updated == null || this.updated < child.updated)
				this.updated = child.updated;
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

	public MetadataLight light() {
		MetadataLight m = new MetadataLight();
		m.setName(name);
		m.setShortName(shortName);
		m.setDescription(description);
		m.setUpdated(updated);
		m.setLocation(location);
		m.setUrl(url);

		return m;
	}

	public void save() throws Exception {
		save(Filesystem.getFileF(Filesystem.FOLDER_SHARED, location, Filesystem.METADATA), this);
	}

	public static Metadata read(String location) {
		File folder = Filesystem.getFolderF(Filesystem.FOLDER_SHARED, location);
		Metadata metadata = (Metadata) read(Metadata.class, Filesystem.getFileF(folder, Filesystem.METADATA));
		metadata.setLocation(location);
		metadata.setShortName(folder.getName());
		metadata.setActive(!Filesystem.getFileF(folder, Filesystem.INACTIVE).exists());
		metadata.setDataset(Filesystem.getFileF(folder, Filesystem.DATASET_DATA).exists());

		return metadata;
	}
}
