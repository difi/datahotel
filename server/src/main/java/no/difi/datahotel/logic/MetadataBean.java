package no.difi.datahotel.logic;

import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.Filesystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Actions on the virtual file system.
 */
@Component("metadata")
public class MetadataBean {

	private static Logger logger = Logger.getLogger(MetadataBean.class.getSimpleName());

	@Autowired
	private UpdateBean updateEJB;
	@Autowired
	private DataBean dataEJB;

	private File root = Filesystem.getFolder(Filesystem.FOLDER_SLAVE);

	@Scheduled(fixedDelay=30000)
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
				if (Filesystem.getFile(f, Filesystem.FILE_METADATA).exists()) {
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

	public List<Metadata> getChildren(String location) {
		List<Metadata> result = new ArrayList<Metadata>();
		File folder = Filesystem.getFolder(FOLDER_SLAVE, location);
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				if (Filesystem.getFile(f, Filesystem.FILE_METADATA).exists()) {
					Metadata m = getChild(location + "/" + f.getName());
					if (m != null)
						result.add(m);
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	public Metadata getChild(String location) {
		try {
			// Read metadata
			return Metadata.read(location);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error while reading " + location);
			return null;
		}
	}

	private String getLocation(File f) {
		return f.toString().substring(root.toString().length() + 1).replace(File.separator, "/");
	}

	public void setUpdateEJB(UpdateBean updateEJB) {
		this.updateEJB = updateEJB;
	}

	public void setDataEJB(DataBean dataEJB) {
		this.dataEJB = dataEJB;
	}
}
