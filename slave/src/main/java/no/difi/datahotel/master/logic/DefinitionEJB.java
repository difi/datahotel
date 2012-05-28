package no.difi.datahotel.master.logic;

import static no.difi.datahotel.util.shared.Filesystem.FILE_DEFINITIONS;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_MASTER_DEFINITION;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import no.difi.datahotel.util.model.Definition;
import no.difi.datahotel.util.model.Definitions;
import no.difi.datahotel.util.model.Disk;
import no.difi.datahotel.util.shared.Filesystem;

@Stateless
public class DefinitionEJB {

	public List<Definition> getDefinitions() {
		List<Definition> list = new ArrayList<Definition>();

		for (File f : Filesystem.getFolder(FOLDER_MASTER_DEFINITION).listFiles())
			if (f.toString().endsWith(".xml"))
				list.add((Definition) Disk.read(Definition.class, f));

		return list;
	}

	public Definition getDefinition(String shortName) {
		File f = Filesystem.getFile(FOLDER_MASTER_DEFINITION, shortName + ".xml");
		return f.isFile() ? (Definition) Disk.read(Definition.class, f) : null;
	}

	public void setDefinition(Definition definition) throws Exception {
		Disk.save(Filesystem.getFile(FOLDER_MASTER_DEFINITION, definition.getShortName() + ".xml"), definition);
		Disk.save(Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS), new Definitions(getDefinitions()));
	}

}
