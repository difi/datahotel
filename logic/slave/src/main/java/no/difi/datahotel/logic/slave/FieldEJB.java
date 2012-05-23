package no.difi.datahotel.logic.slave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Singleton;

import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Fields;
import no.difi.datahotel.util.bridge.Metadata;

@Singleton
public class FieldEJB {
	
	private HashMap<String, List<Field>> fields = new HashMap<String, List<Field>>();
	private HashMap<String, Definition> definitions = new HashMap<String, Definition>();
	private HashMap<String, List<String>> defUsage = new HashMap<String, List<String>>();
	
	public void update(Metadata metadata) {
		Logger logger = metadata.getLogger();

		logger.info("Reading fields.");
		
		if (fields.containsKey(metadata.getLocation())) {
			for (Field f : fields.get(metadata.getLocation())) {
				Definition d = f.getDefinition();
				defUsage.get(d.getShortName()).remove(metadata.getLocation());
			}
		}
		
		Fields fresh = Fields.read(metadata.getLocation());
		
		fields.put(metadata.getLocation(), fresh.getFields());
		for (Field f : fresh.getFields()) {
			Definition d = f.getDefinition();
			if (!definitions.containsKey(d.getShortName()))
				definitions.put(d.getShortName(), d);
			if (!defUsage.containsKey(d.getShortName()))
				defUsage.put(d.getShortName(), new ArrayList<String>());
			if (!defUsage.get(d.getShortName()).contains(metadata.getLocation()))
				defUsage.get(d.getShortName()).add(metadata.getLocation());
		}
	}
	
	public List<Field> getFields(Metadata metadata) {
		return fields.get(metadata.getLocation());
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
