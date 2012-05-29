package no.difi.datahotel.util.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class creates a Field object that represent the fields in the dataset, in
 * XML format
 */
@XmlRootElement
public class FieldLight {

	private String name;
	private String shortName;
	private Boolean groupable;
	private Boolean searchable;
	private Boolean indexPrimaryKey;
	private String description;
	private String definition;

	public FieldLight(Field field) {
		name = field.getName();
		shortName = field.getShortName();
		groupable = field.getGroupable();
		searchable = field.getSearchable();
		indexPrimaryKey = field.getIndexPrimaryKey();
		description = field.getContent();
		definition = field.getDefShort();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Boolean getGroupable() {
		return groupable;
	}

	public void setGroupable(Boolean groupable) {
		this.groupable = groupable;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	public Boolean getIndexPrimaryKey() {
		return indexPrimaryKey;
	}

	public void setIndexPrimaryKey(Boolean indexPrimaryKey) {
		this.indexPrimaryKey = indexPrimaryKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

}
