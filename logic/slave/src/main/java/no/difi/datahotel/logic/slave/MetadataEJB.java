package no.difi.datahotel.logic.slave;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.bridge.MetadataLight;
import no.difi.datahotel.util.shared.Filesystem;

@Singleton
public class MetadataEJB {

	private static Logger logger = Logger.getLogger(MetadataEJB.class.getSimpleName());

	@EJB
	private FieldEJB fieldEJB;
	@EJB
	private ChunkEJB chunkEJB;
	@EJB
	private IndexEJB indexEJB;

	private File root = Filesystem.getFolderF(Filesystem.FOLDER_SHARED);
	private Map<String, Metadata> directory = new HashMap<String, Metadata>();
	private Map<String, Long> timestamps = new HashMap<String, Long>();

	
	@Schedule(second = "0,15,30,45", minute = "*", hour = "*")
	public void update() {
		try {
			Metadata mroot = new Metadata();
			Map<String, Metadata> mdir = new HashMap<String, Metadata>();
			mdir.put("", mroot);
	
			updateRecursive(mroot, mdir, root);
	
			directory = mdir;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to update metadata", e);
		}
	}

	private void updateRecursive(Metadata parent, Map<String, Metadata> directory, File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				if (Filesystem.getFileF(f, Filesystem.METADATA).exists()) {
					// Read metadata
					Metadata m = Metadata.read(getLocation(f));

					// Do the recursion
					updateRecursive(m, directory, f);
					
					// Register metadata
					parent.addChild(m);
					directory.put(m.getLocation(), m);

					// Make data available
					if (m.isDataset())
						validate(m);
				}
			}
		}
	}

	@Asynchronous
	private void validate(Metadata metadata) {
		if (metadata.getUpdated() == null) {
			logger.warning("[" + metadata.getLocation() + "] Missing timestamp in metadata file.");
			return;
		}

		if (metadata.getUpdated() == getTimestamp(metadata.getLocation()))
			return;

		if (getTimestamp(metadata.getLocation()) != null && getTimestamp(metadata.getLocation()) == -1) {
			logger.info("[" + metadata.getLocation() + "] Do not disturb.");
			return;
		}

		setTimestamp(metadata.getLocation(), -1L);

		String[] l = metadata.getLocation().split("/");

		fieldEJB.update(metadata);
		chunkEJB.update(metadata);
		indexEJB.update(l[0], l[1], l[2], metadata.getUpdated());

		logger.info("[" + metadata.getLocation() + "] Ready");
		setTimestamp(metadata.getLocation(), metadata.getUpdated());
	}

	private String getLocation(File f) {
		return f.toString().substring(root.toString().length() + 1).replace(File.separator, "/");
	}

	public List<MetadataLight> getChildren(String... dir) {
		String location = dir.length == 0 ? "" : Metadata.getLocation(dir);
		if (directory.containsKey(location)) {
			List<MetadataLight> children = new ArrayList<MetadataLight>();
			for (Metadata m : directory.get(location).getChildren().values())
				if (m.isActive())
					children.add(m.light());
			return children.size() == 0 ? null : children;
		}
		return null;
	}

	public Metadata getChild(String... dir) {
		String location = Metadata.getLocation(dir);
		return directory.containsKey(location) ? directory.get(location) : null;
	}

	public List<Metadata> getDatasets() {
		List<Metadata> datasets = new ArrayList<Metadata>();
		for (Metadata m : directory.values())
			if (m.isDataset() && m.isActive())
				datasets.add(m);
		return datasets;
	}

	public Long getTimestamp(String location) {
		return timestamps.get(location);
	}
	
	public void setTimestamp(String location, Long timestamp) {
		timestamps.put(location, timestamp);
	}
}
