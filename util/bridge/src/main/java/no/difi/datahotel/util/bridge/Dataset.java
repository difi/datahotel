package no.difi.datahotel.util.bridge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import no.difi.datahotel.util.shared.Filesystem;

/**
 * The class crates a Metadata object that represent a dataset in XML format.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Dataset extends Abstract implements Comparable<Dataset> {

	/**
	 * The name of the dataset
	 */
	@XmlElement
	private String name;

	/**
	 * The short name of the dataset
	 */
	@XmlElement
	private String shortName;

	/**
	 * The description of the dataset
	 */
	@XmlElement
	private String description;

	/**
	 * The datasetGroup the dataset belongs to
	 */
	@XmlElement
	private String group;

	/**
	 * The owner of the dataset
	 */
	@XmlElement
	private String owner;

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

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getShortName() {
		return shortName;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group.getShortName();
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner.getShortName();
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof Dataset))
			return false;

		Dataset d = (Dataset) o;

		if (owner != null && !owner.equals(d.owner))
			return false;
		if (group != null && !group.equals(d.group))
			return false;
		if (shortName != null && !shortName.equals(d.shortName))
			return false;

		// TODO Make better

		return true;
	}

	public void save() throws Exception {
		save(Filesystem.getFileF(Filesystem.FOLDER_SHARED, owner, group, shortName, Filesystem.DATASET_METADATA), this);
	}

	public static Dataset read(String owner, String group, String dataset) {
		return (Dataset) read(Dataset.class,
				Filesystem.getFileF(Filesystem.FOLDER_SHARED, owner, group, dataset, Filesystem.DATASET_METADATA));
	}

	@Override
	public int compareTo(Dataset other) {
		return this.getName().compareTo(other.getName());
	}
}
