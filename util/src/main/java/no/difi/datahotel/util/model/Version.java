package no.difi.datahotel.util.model;

import java.io.File;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Version {

	private long timestamp;

	public Version() {

	}

	public Version(File folder) {
		this.timestamp = Long.parseLong(folder.getName());
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
