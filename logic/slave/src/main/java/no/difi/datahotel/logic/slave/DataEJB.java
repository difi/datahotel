package no.difi.datahotel.logic.slave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;

import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.bridge.MetadataLight;

@Singleton
public class DataEJB {

	private Map<String, Metadata> directory = new HashMap<String, Metadata>();
	private Map<String, Long> timestamps = new HashMap<String, Long>();

	public List<MetadataLight> getChildren(String... dir) {
		String location = dir.length == 0 ? "" : Metadata.getLocation(dir);
		if (directory.containsKey(location)) {
			List<MetadataLight> children = new ArrayList<MetadataLight>();
			for (Metadata m : directory.get(location).getChildren())
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

	public void setDirectory(Map<String, Metadata> directory) {
		this.directory = directory;
	}
}
