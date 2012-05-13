package no.difi.datahotel.util.bridge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import no.difi.datahotel.util.shared.Filesystem;

/**
 * The class creates a Owner object that represents the owner of the dataset in
 * XML format
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Owner extends Abstract implements Comparable<Owner> {
	@XmlElement
	private String name;

	@XmlElement
	private String shortName;

	@XmlElement
	private String url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof Owner))
			return false;

		Owner d = (Owner) o;

		if (shortName.equals(d.shortName))
			return true;

		return false;
	}
	
	public void save() throws Exception {
		save(Filesystem.getFileF(Filesystem.FOLDER_SHARED, shortName, Filesystem.OWNER_METADATA), this);
	}

	public static Owner read(String owner) {
		return (Owner) read(Owner.class, Filesystem.getFileF(Filesystem.FOLDER_SHARED, owner, Filesystem.OWNER_METADATA));
	}

	@Override
	public int compareTo(Owner other) {
		return shortName.compareTo(other.shortName);
	}
}
