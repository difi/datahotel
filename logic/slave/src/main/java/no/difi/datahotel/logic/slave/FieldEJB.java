package no.difi.datahotel.logic.slave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;

import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Fields;

@Stateless
public class FieldEJB {
	
	private HashMap<String, List<Field>> fields = new HashMap<String, List<Field>>();
	private HashMap<String, Definition> definitions = new HashMap<String, Definition>();
	private HashMap<String, List<String>> defUsage = new HashMap<String, List<String>>();
	
	public void update(String owner, String group, String dataset) {
		String location = owner + "/" + group + "/" + dataset;

		if (fields.containsKey(location)) {
			for (Field f : fields.get(location)) {
				Definition d = f.getDefinition();
				defUsage.get(d.getShortName()).remove(location);
			}
		}
		
		Fields fresh = Fields.read(owner, group, dataset);
		
		fields.put(location, fresh.getFields());
		for (Field f : fresh.getFields()) {
			Definition d = f.getDefinition();
			if (!definitions.containsKey(d.getShortName()))
				definitions.put(d.getShortName(), d);
			if (!defUsage.containsKey(d.getShortName()))
				defUsage.put(d.getShortName(), new ArrayList<String>());
			if (!defUsage.get(d.getShortName()).contains(location))
				defUsage.get(d.getShortName()).add(location);
		}
	}
	
	public List<Field> getFields(String owner, String group, String dataset) {
		String location = owner + "/" + group + "/" + dataset;
		return fields.get(location);
	}
	
	public List<Definition> getDefinitions() {
		return new ArrayList<Definition>(definitions.values());
	}
	
	public Definition getDefinition(String def) {
		return definitions.get(def);
	}
	
	public List<String> getUsage(String definition) {
		return defUsage.get(definition);
	}
}
