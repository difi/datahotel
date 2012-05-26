package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.FILE_DEFINITIONS;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Singleton;

import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.DefinitionLight;
import no.difi.datahotel.util.bridge.Definitions;
import no.difi.datahotel.util.bridge.Disk;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.FieldLight;
import no.difi.datahotel.util.bridge.Fields;
import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.shared.Filesystem;

@Singleton
public class FieldEJB {

	private Map<String, List<Field>> fields = new HashMap<String, List<Field>>();
	private Map<String, Definition> definitions = new HashMap<String, Definition>();

	private long defUpdated;
	
	public void updateDefinitions() {
		File f = Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS);
		if (f.isFile() && f.lastModified() != defUpdated) {
			Map<String, Definition> definitions = new HashMap<String, Definition>();
			
			for (Definition def : ((Definitions) Disk.read(Definitions.class, f)).getDefinitions())
				definitions.put(def.getShortName(), def);

			this.definitions = definitions;
			defUpdated = f.lastModified();
		}
	}
	
	public void update(Metadata metadata) {
		updateDefinitions();
		
		Logger logger = metadata.getLogger();

		logger.info("Reading fields.");
		
		/*if (fields.containsKey(metadata.getLocation())) {
			for (Field f : fields.get(metadata.getLocation())) {
				Definition d = f.getDefinition();
			}
		}*/
		
		Fields fresh = Fields.read(metadata.getLocation());
		
		fields.put(metadata.getLocation(), fresh.getFields());
		/*for (Field f : fresh.getFields()) {
			Definition d = f.getDefinition();
		}*/
	}
	
	public List<FieldLight> getFields(Metadata metadata) {
		List<FieldLight> result = new ArrayList<FieldLight>();
		for (Field f : fields.get(metadata.getLocation()))
			result.add(f.light());
		return result;
	}
	
	public List<DefinitionLight> getDefinitions() {
		List<DefinitionLight> result = new ArrayList<DefinitionLight>();
		for (Definition d : definitions.values())
			result.add(d.light());
		return result;
	}
	
	public DefinitionLight getDefinition(String def) {
		if (definitions.containsKey(def))
			return definitions.get(def).light();
		return null;
	}
	
	public List<String> getUsage(String definition) {
		// TODO Fix me
		return null;
	}
}
