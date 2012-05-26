package no.difi.datahotel.util.bridge;

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

	public Boolean getGroupable() {
		return groupable;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public Boolean getIndexPrimaryKey() {
		return indexPrimaryKey;
	}

	public String getDefinition() {
		return definition;
	}

	public String getShortName() {
		return shortName;
	}
}
