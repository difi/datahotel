package no.difi.datahotel.util.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import no.difi.datahotel.util.shared.Filesystem;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Structure extends Abstract {

	@XmlElement(name = "owners")
	@XmlJavaTypeAdapter(Map_Owner_Adapter.class)
	private Map<String, Map<String, Map<String, Long>>> structure = new HashMap<String, Map<String, Map<String, Long>>>();

	public void setStructure(Map<String, Map<String, Map<String, Long>>> structure) {
		this.structure = structure;
	}

	public Map<String, Map<String, Map<String, Long>>> getStructure() {
		return structure;
	}

	public static class Map_Owner_Adapter extends XmlAdapter<JaxbOwnerMap, Map<String, Map<String, Map<String, Long>>>> {

		@Override
		public JaxbOwnerMap marshal(Map<String, Map<String, Map<String, Long>>> v) throws Exception {
			JaxbOwnerMap jaxbMap = new JaxbOwnerMap();
			List<JaxbOwnerEntry> aList = jaxbMap.getPairs();
			for (Map.Entry<String, Map<String, Map<String, Long>>> e : v.entrySet()) {
				aList.add(new JaxbOwnerEntry(e.getKey(), e.getValue()));
			}
			return jaxbMap;
		}

		@Override
		public Map<String, Map<String, Map<String, Long>>> unmarshal(JaxbOwnerMap v) throws Exception {
			Map<String, Map<String, Map<String, Long>>> map = new HashMap<String, Map<String, Map<String, Long>>>();
			for (JaxbOwnerEntry e : v.getPairs()) {
				map.put(e.getKey(), e.getValue());
			}
			return map;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "owners")
	public static class JaxbOwnerMap {
		@XmlElement(name = "owner", required = true)
		private final List<JaxbOwnerEntry> pairs = new ArrayList<JaxbOwnerEntry>();

		public List<JaxbOwnerEntry> getPairs() {
			return this.pairs;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "element")
	public static class JaxbOwnerEntry {

		@XmlElement(name = "name", required = true)
		private final String key;
		@XmlElement(name = "datasetGroups", required = true)
		@XmlJavaTypeAdapter(Map_Datasetgroup_Adapter.class)
		private final Map<String, Map<String, Long>> value;

		public JaxbOwnerEntry(String key, Map<String, Map<String, Long>> value) {
			this.key = key;
			this.value = value;
		}

		public JaxbOwnerEntry() {
			this.key = null;
			this.value = null;
		}

		public String getKey() {
			return key;
		}

		public Map<String, Map<String, Long>> getValue() {
			return value;
		}
	}

	public static class Map_Datasetgroup_Adapter extends
			XmlAdapter<JaxbDatasetgroupMap, Map<String, Map<String, Long>>> {

		@Override
		public JaxbDatasetgroupMap marshal(Map<String, Map<String, Long>> v) throws Exception {
			JaxbDatasetgroupMap jaxbMap = new JaxbDatasetgroupMap();
			List<JaxbDatasetgroupEntry> aList = jaxbMap.getPairs();
			for (Map.Entry<String, Map<String, Long>> e : v.entrySet()) {
				aList.add(new JaxbDatasetgroupEntry(e.getKey(), e.getValue()));
			}
			return jaxbMap;
		}

		@Override
		public Map<String, Map<String, Long>> unmarshal(JaxbDatasetgroupMap v) throws Exception {
			Map<String, Map<String, Long>> map = new HashMap<String, Map<String, Long>>();
			for (JaxbDatasetgroupEntry e : v.getPairs()) {
				map.put(e.getKey(), e.getValue());
			}
			return map;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "datasetGroups")
	public static class JaxbDatasetgroupMap {
		@XmlElement(name = "datasetGroup", required = true)
		private final List<JaxbDatasetgroupEntry> pairs = new ArrayList<JaxbDatasetgroupEntry>();

		public List<JaxbDatasetgroupEntry> getPairs() {
			return this.pairs;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "element")
	public static class JaxbDatasetgroupEntry {

		@XmlElement(name = "name", required = true)
		private final String key;
		@XmlElement(name = "datasets", required = true)
		@XmlJavaTypeAdapter(Map_Dataset_Adapter.class)
		private final Map<String, Long> value;

		public JaxbDatasetgroupEntry(String key, Map<String, Long> value) {
			this.key = key;
			this.value = value;
		}

		public JaxbDatasetgroupEntry() {
			this.key = null;
			this.value = null;
		}

		public String getKey() {
			return key;
		}

		public Map<String, Long> getValue() {
			return value;
		}
	}

	public static class Map_Dataset_Adapter extends XmlAdapter<JaxbDatasetMap, Map<String, Long>> {

		@Override
		public JaxbDatasetMap marshal(Map<String, Long> v) throws Exception {
			JaxbDatasetMap myMap = new JaxbDatasetMap();
			List<JaxbDatasetEntry> aList = myMap.getPairs();
			for (Map.Entry<String, Long> e : v.entrySet()) {
				aList.add(new JaxbDatasetEntry(e.getKey(), e.getValue()));
			}
			return myMap;
		}

		@Override
		public Map<String, Long> unmarshal(JaxbDatasetMap v) throws Exception {
			Map<String, Long> map = new HashMap<String, Long>();
			for (JaxbDatasetEntry e : v.getPairs()) {
				map.put(e.getKey(), e.getValue());
			}
			return map;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "datasets")
	public static class JaxbDatasetMap {
		@XmlElement(name = "dataset", required = true)
		private final List<JaxbDatasetEntry> pairs = new ArrayList<JaxbDatasetEntry>();

		public List<JaxbDatasetEntry> getPairs() {
			return this.pairs;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "element")
	public static class JaxbDatasetEntry {

		@XmlElement(name = "name", required = true)
		private final String key;
		@XmlElement(name = "lastEdited", required = true)
		private final Long value;

		public JaxbDatasetEntry(String key, Long value) {
			this.key = key;
			this.value = value;
		}

		public JaxbDatasetEntry() {
			this.key = null;
			this.value = null;
		}

		public String getKey() {
			return key;
		}

		public Long getValue() {
			return value;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof Structure))
			return false;

		Structure s = (Structure) o;

		if (structure == null && s.structure == null)
			return true;
		if (structure == null || s.structure == null)
			return false;
		return structure.equals(s.structure);
	}

	public void save() throws Exception {
		save(Filesystem.getFileF(Filesystem.FOLDER_SHARED, Filesystem.STRUCTURE), this);
	}

	public static Structure read() {
		return (Structure) read(Structure.class, Filesystem.getFileF(Filesystem.FOLDER_SHARED, Filesystem.STRUCTURE));
	}
}
