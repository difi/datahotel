package no.difi.datahotel.util.bridge;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.NONE)
public class Definitions extends Abstract {

	@XmlElementWrapper(name = "definitions")
	@XmlElement(name = "definition")
	private List<Definition> definitions = new ArrayList<Definition>();

	public Definitions() {
		
	}
	
	public Definitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

}
