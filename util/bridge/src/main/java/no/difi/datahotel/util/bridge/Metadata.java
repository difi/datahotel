package no.difi.datahotel.util.bridge;

import java.io.File;

import javax.xml.bind.annotation.XmlRootElement;

import no.difi.datahotel.util.shared.Filesystem;

@XmlRootElement
public class Metadata extends Abstract {

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

	public static MetadataSlave read(String location) {
		File folder = Filesystem.getFolderF(Filesystem.FOLDER_SHARED, location);
		MetadataSlave metadata = (MetadataSlave) read(MetadataSlave.class, Filesystem.getFileF(folder, Filesystem.METADATA));
		metadata.setLocation(location);
		metadata.setShortName(folder.getName());
		metadata.setActive(!Filesystem.getFileF(folder, Filesystem.INACTIVE).exists());
		metadata.setDataset(Filesystem.getFileF(folder, Filesystem.DATASET_DATA).exists());

		return metadata;
	}
}
