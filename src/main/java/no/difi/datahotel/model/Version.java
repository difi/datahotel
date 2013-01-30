package no.difi.datahotel.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Version {

	private long timestamp;
	private String description;
	private String error;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
