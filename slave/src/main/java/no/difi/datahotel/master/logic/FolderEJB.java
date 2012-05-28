package no.difi.datahotel.master.logic;

import static no.difi.datahotel.util.shared.Filesystem.FILE_METADATA;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import no.difi.datahotel.util.model.Disk;
import no.difi.datahotel.util.model.Metadata;
import no.difi.datahotel.util.model.MetadataLight;
import no.difi.datahotel.util.shared.Filesystem;

@Stateless
public class FolderEJB {

	public List<MetadataLight> getFolders() {
		List<MetadataLight> result = new ArrayList<MetadataLight>();
		
		for (File f : Filesystem.getFolder(FOLDER_SLAVE).listFiles()) {
			File file = Filesystem.getFile(f, FILE_METADATA);
			if (f.isDirectory() && file.exists())
				result.add(((Metadata) Disk.read(Metadata.class, file)).light());
		}
		
		return result;
	}
	
}
