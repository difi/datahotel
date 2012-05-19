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
import no.difi.datahotel.util.bridge.MetadataSlave;
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
	private Map<String, MetadataSlave> directory = new HashMap<String, MetadataSlave>();
	private Map<String, Long> timestamps = new HashMap<String, Long>();

	
	@Schedule(second = "0,15,30,45", minute = "*", hour = "*")
	public void update() {
		MetadataSlave mroot = new MetadataSlave();
		Map<String, MetadataSlave> mdir = new HashMap<String, MetadataSlave>();
		mdir.put("", mroot);

		updateRec(mroot, mdir, root);

		directory = mdir;
	}

	private void updateRec(MetadataSlave metadata, Map<String, MetadataSlave> mdir, File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				File fm = Filesystem.getFileF(f, Filesystem.METADATA);
				if (fm.exists()) {
					MetadataSlave m = Metadata.read(getLocation(f));
					metadata.getChildren().put(f.getName(), m);
					mdir.put(m.getLocation(), m);

					if (m.isDataset())
						validate(m);

					updateRec(m, mdir, f);
				}
			}
		}
	}

	@Asynchronous
	private void validate(MetadataSlave metadata) {
		try {
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

			fieldEJB.update(l[0], l[1], l[2]);
			chunkEJB.update(l[0], l[1], l[2], metadata.getUpdated());
			indexEJB.update(l[0], l[1], l[2], metadata.getUpdated());

			logger.info("[" + metadata.getLocation() + "] Ready");
			setTimestamp(metadata.getLocation(), metadata.getUpdated());
		} catch (Exception e) {
			logger.log(Level.WARNING, "[" + metadata.getLocation() + "] Exception", e);
		}
	}

	private String getLocation(File f) {
		return f.toString().substring(root.toString().length() + 1).replace(File.separator, "/");
	}

	private String getLocation(String... dir) {
		String location = dir[0];
		for (int i = 1; i < dir.length; i++)
			location += "/" + dir[i];
		return location;
	}

	public List<Metadata> getChildren(String... dir) {
		String location = dir.length == 0 ? "" : getLocation(dir);
		if (directory.containsKey(location)) {
			List<Metadata> children = new ArrayList<Metadata>();
			for (MetadataSlave m : directory.get(location).getChildren().values())
				if (m.isActive())
					children.add(m);
			return children.size() == 0 ? null : children;
		}
		return null;
	}

	public Metadata getChild(String... dir) {
		String location = getLocation(dir);
		return directory.containsKey(location) ? directory.get(location) : null;
	}

	public List<Metadata> getDatasets() {
		List<Metadata> datasets = new ArrayList<Metadata>();
		for (MetadataSlave m : directory.values())
			if (m.isDataset() && m.isActive())
				datasets.add(m);
		return datasets;
	}

	public Map<String, MetadataSlave> getDirectory() {
		return directory;
	}

	public void setDirectory(Map<String, MetadataSlave> directory) {
		this.directory = directory;
	}
	
	public Long getTimestamp(String location) {
		return timestamps.get(location);
	}
	
	public void setTimestamp(String location, Long timestamp) {
		timestamps.put(location, timestamp);
	}
}
