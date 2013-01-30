package no.difi.datahotel.slave.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.MetadataLight;
import no.difi.datahotel.util.DatahotelException;

import org.springframework.stereotype.Component;

@Component("data")
public class DataBean {

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
		if (!directory.containsKey(location))
			throw new DatahotelException(404, "Dataset or folder not found.");
		
		return directory.get(location);
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
