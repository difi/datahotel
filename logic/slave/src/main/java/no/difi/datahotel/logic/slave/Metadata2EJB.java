package no.difi.datahotel.logic.slave;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.shared.Filesystem;

@Singleton
public class Metadata2EJB {

	@EJB
	private SystemEJB systemEJB;
	
	private File root = Filesystem.getFolderF(Filesystem.FOLDER_SHARED);

	@Schedule(second = "0,15,30,45", minute = "*", hour = "*")
	public void schedule() {
		//if (!systemEJB.isReady())
		//	return;

		update();
	}
	
	public void update() {
		Metadata mroot = new Metadata();
		Map<String, Metadata> mdir = new HashMap<String, Metadata>();
		mdir.put("", mroot);

		updateRec(mroot, mdir, root);

		for (String key : mdir.keySet())
			System.out.println(key);

		systemEJB.setDirectory(mdir);
	}

	private void updateRec(Metadata metadata, Map<String, Metadata> mdir, File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				File fm = Filesystem.getFileF(f, Filesystem.METADATA);
				if (fm.exists()) {
					Metadata m = Metadata.read(getLocation(f));
					metadata.getChildren().put(f.getName(), m);
					mdir.put(m.getLocation(), m);
					updateRec(metadata, mdir, f);
				}
			}
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
		if (systemEJB.getDirectory().containsKey(location)) {
			List<Metadata> children = new ArrayList<Metadata>();
			for (Metadata m : systemEJB.getDirectory().get(location).getChildren().values())
				if (m.isActive())
					children.add(m);
			return children.size() == 0 ? null : children;
		}
		return null;
	}

	public Metadata getChild(String... dir) {
		String location = getLocation(dir);
		return systemEJB.getDirectory().containsKey(location) ? systemEJB.getDirectory().get(location) : null;
	}

	public List<Metadata> getDatasets() {
		List<Metadata> datasets = new ArrayList<Metadata>();
		for (Metadata m : systemEJB.getDirectory().values())
			if (m.isDataset() && m.isActive())
				datasets.add(m);
		return datasets;
	}
}
