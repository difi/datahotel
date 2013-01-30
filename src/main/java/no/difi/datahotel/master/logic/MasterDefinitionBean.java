package no.difi.datahotel.master.logic;

import static no.difi.datahotel.util.Filesystem.FILE_DEFINITIONS;
import static no.difi.datahotel.util.Filesystem.FOLDER_MASTER_DEFINITION;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import no.difi.datahotel.model.Definition;
import no.difi.datahotel.model.Definitions;
import no.difi.datahotel.util.Disk;
import no.difi.datahotel.util.Filesystem;

@Component("masterDefinition")
public class MasterDefinitionBean {

	/**
	 * Read all definitions from disk.
	 */
	public List<Definition> getDefinitions() {
		List<Definition> list = new ArrayList<Definition>();

		for (File f : Filesystem.getFolder(FOLDER_MASTER_DEFINITION).listFiles())
			if (f.toString().endsWith(".xml"))
				list.add((Definition) Disk.read(Definition.class, f));

		return list;
	}

	/**
	 * Read a definitions from disk.
	 */
	public Definition getDefinition(String shortName) {
		File f = Filesystem.getFile(FOLDER_MASTER_DEFINITION, shortName + ".xml");
		return f.isFile() ? (Definition) Disk.read(Definition.class, f) : null;
	}

	/**
	 * Saves a definiton to disk and  
	 */
	public void setDefinition(Definition definition) throws Exception {
		Disk.save(Filesystem.getFile(FOLDER_MASTER_DEFINITION, definition.getShortName() + ".xml"), definition);
		Disk.save(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS), new Definitions(getDefinitions()));
	}

}
