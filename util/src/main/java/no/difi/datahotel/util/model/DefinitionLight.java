package no.difi.datahotel.util.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class creates a definintion that represent the 
 * definiton of a field in a dataset.
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

	public String getDescription() {
		return description;
	}

	public String getShortName() {
		return shortName;
	}
	
	@Override
	public int compareTo(DefinitionLight other) {
		return name.compareTo(other.name);
	}
}
