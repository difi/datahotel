package no.difi.datahotel.util.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "metadata")
public class MetadataLight {

	private String shortName;
	private String name;
	private String description;
	private String url;
	private String location;
	private Long updated;

	public MetadataLight() {
		
	}
	
	public MetadataLight(Metadata metadata) {
		shortName = metadata.getShortName();
		name = metadata.getName();
		description = metadata.getDescription();
		url = metadata.getUrl();
		location = metadata.getLocation();
		updated = metadata.getUpdated();
	}
	
	public String getShortName() {
		return shortName;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public String getLocation() {
		return location;
	}

	public Long getUpdated() {
		return updated;
	}
}