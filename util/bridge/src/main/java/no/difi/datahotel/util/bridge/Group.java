package no.difi.datahotel.util.bridge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import no.difi.datahotel.util.shared.Filesystem;

/**
 * The class creates a DatasetGroup object that represent the DatasetGroup the
 * dataset belongs to in XML format
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Group extends Abstract {
	@XmlElement
	private String name;

	@XmlElement
	private String shortName;

	@XmlElement
	private String url;

	@XmlElement
	private String description;

	@XmlElement
	private String owner;
	
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

	public void setDescription(String deskription) {
		this.description = deskription;
	}

	public String getDescription() {
		return description;
	}
	
	public void setOwner(Owner owner) {
		this.owner = owner.getShortName();
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getOwner() {
		return this.owner;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof Group))
			return false;

		Group d = (Group) o;

		if (owner != null && !owner.equals(d.owner))
			return false;
		if (shortName != null && !shortName.equals(d.shortName))
			return false;

		// TODO Make better
		
		return true;
	}
	
	public void save() throws Exception {
		save(Filesystem.getFileF(Filesystem.FOLDER_SHARED, owner, shortName, Filesystem.GROUP_METADATA), this);
	}

	public static Group read(String owner, String group) {
		return (Group) read(Group.class, Filesystem.getFileF(Filesystem.FOLDER_SHARED, owner, group, Filesystem.GROUP_METADATA));
	}
}
