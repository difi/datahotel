package no.difi.datahotel.util.bridge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class creates a Field object that represent the fields in the dataset, in
 * XML format
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Field {
	@XmlElement
	private String name;

	@XmlElement
	private String shortName;

	@XmlElement
	private Boolean groupable;

	@XmlElement
	private Boolean searchable;

	@XmlElement
	private Boolean indexPrimaryKey;

	@XmlAttribute
	private int columnNumber;

	@XmlElement
	private String content;

	@XmlElement
	private Definition definition;

	public Field() {

	}

	public Field(String shortName, boolean searchable) {
		this.shortName = shortName;
		this.searchable = searchable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getGroupable() {
		return groupable;
	}

	public void setGroupable(Boolean groupable) {
		this.groupable = groupable;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String metadata) {
		this.content = metadata;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setIndexPrimaryKey(Boolean indexPrimaryKey) {
		this.indexPrimaryKey = indexPrimaryKey;
	}

	public Boolean getIndexPrimaryKey() {
		return indexPrimaryKey;
	}

	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	public Definition getDefinition() {
		return definition;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getShortName() {
		return shortName;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof Field))
			return false;

		Field f = (Field) o;

		if (shortName != null && !shortName.equals(f.shortName))
			return false;

		// TODO Make better

		return true;
	}
}
