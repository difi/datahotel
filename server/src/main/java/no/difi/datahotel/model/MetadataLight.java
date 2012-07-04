package no.difi.datahotel.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "metadata")
public class MetadataLight {

	private String shortName;
	private String name;
	private String description;
	private String url;
	private String location;
	private Long updated;
	private boolean dataset;

	public MetadataLight() {

	}

	public MetadataLight(Metadata metadata) {
		shortName = metadata.getShortName();
		name = metadata.getName();
		description = metadata.getDescription();
		url = metadata.getUrl();
		location = metadata.getLocation();
		updated = metadata.getUpdated();
		dataset = metadata.isDataset();
	}

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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Long getUpdated() {
		return updated;
	}

	public void setUpdated(Long updated) {
		this.updated = updated;
	}

	public boolean isDataset() {
		return dataset;
	}

	public void setDataset(boolean dataset) {
		this.dataset = dataset;
	}

}