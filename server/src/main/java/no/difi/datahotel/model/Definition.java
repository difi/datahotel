package no.difi.datahotel.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class creates a definintion that represent the 
 * definiton of a field in a dataset.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Definition implements Comparable<Definition>, Light<DefinitionLight> {
	@XmlElement
	private String name;
	
	@XmlElement
	private String shortName;

	@XmlElement
	private String description;
	
	private List<Field> fields = new ArrayList<Field>();

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
		this.description = "".equals(description) ? null : description;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getShortName() {
		return shortName;
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public void addField(Field field) {
		fields.add(field);
	}

	public DefinitionLight light() {
		return new DefinitionLight(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) 
			return true;
		if (o == null || !(o instanceof Definition))
			return false;

		Definition d = (Definition) o;
		
		if (shortName != null && !shortName.equals(d.shortName))
			return false;
		// TODO Make better.
		
		return true;
	}

	@Override
	public int compareTo(Definition other) {
		return name.compareTo(other.name);
	}
}
