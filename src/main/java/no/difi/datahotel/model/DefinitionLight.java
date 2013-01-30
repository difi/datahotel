package no.difi.datahotel.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class creates a definintion that represent the definiton of a field in a
 * dataset.
 */
@XmlRootElement
public class DefinitionLight implements Comparable<DefinitionLight> {

	private String name;
	private String shortName;
	private String description;

	public DefinitionLight(Definition definition) {
		name = definition.getName();
		shortName = definition.getShortName();
		description = definition.getDescription();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(DefinitionLight other) {
		return name.compareTo(other.name);
	}
}
