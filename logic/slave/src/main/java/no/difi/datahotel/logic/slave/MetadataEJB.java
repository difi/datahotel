package no.difi.datahotel.logic.slave;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.shared.Filesystem;

@Stateless
public class MetadataEJB {

	private static Logger logger = Logger.getLogger(MetadataEJB.class.getSimpleName());

	@EJB
	private UpdateEJB updateEJB;
	@EJB
	private DataEJB dataEJB;

	private File root = Filesystem.getFolderF(Filesystem.FOLDER_SHARED);

	@Schedule(second = "0,30", minute = "*", hour = "*")
	public void update() {
		Metadata mroot = new Metadata();
		Map<String, Metadata> mdir = new HashMap<String, Metadata>();
		mdir.put("", mroot);

		updateRecursive(mroot, mdir, root);

		dataEJB.setDirectory(mdir);
	}

	private void updateRecursive(Metadata parent, Map<String, Metadata> directory, File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				if (Filesystem.getFileF(f, Filesystem.METADATA).exists()) {
					try {
						// Read metadata
						Metadata m = Metadata.read(getLocation(f));
	
						// Do the recursion
						updateRecursive(m, directory, f);
						
						// Register metadata
						parent.addChild(m);
						directory.put(m.getLocation(), m);
	
						// Make data available
						if (m.isDataset())
							updateEJB.validate(m);
					} catch (Exception e) {
						logger.log(Level.WARNING, "Error while reading " + getLocation(f));						
					}
				}
			}
		}
		Collections.sort(parent.getChildren());
	}

	private String getLocation(File f) {
		return f.toString().substring(root.toString().length() + 1).replace(File.separator, "/");
	}
}
