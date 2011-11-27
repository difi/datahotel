package no.difi.datahotel.logic.slave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import no.difi.datahotel.util.bridge.Dataset;
import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Fields;
import no.difi.datahotel.util.bridge.Group;
import no.difi.datahotel.util.bridge.Owner;

@Singleton
public class MetadataEJB {

	private static Logger logger = Logger.getLogger(MetadataEJB.class.getSimpleName());
	
	@EJB
	private StructureEJB structureEJB;

	// TODO La disse ha en oppstartsverdi.
	private Map<String, Owner> owners = new HashMap<String, Owner>();
	private Map<String, Map<String, Group>> groups = new HashMap<String, Map<String,Group>>();
	private Map<String, Map<String, Map<String, Dataset>>> datasets = new HashMap<String, Map<String,Map<String,Dataset>>>();
	private Map<String, Map<String, Map<String, Fields>>> fields = new HashMap<String, Map<String,Map<String,Fields>>>();
	private TreeSet<DatasetHolder> list = new TreeSet<MetadataEJB.DatasetHolder>();
	private Map<String, Definition> defList = new HashMap<String, Definition>();
	private Map<String, Set<Dataset>> defDataset = new HashMap<String, Set<Dataset>>();
	
	private List<Dataset> allDatasets = new ArrayList<Dataset>();
	private List<Definition> allDefinitions = new ArrayList<Definition>();

	@Schedule(second = "0", minute = "*/3", hour = "*")
	@PostConstruct
	public void update() {
		Map<String, Owner> owners = new HashMap<String, Owner>();
		Map<String, Map<String, Group>> groups = new HashMap<String, Map<String, Group>>();
		Map<String, Map<String, Map<String, Dataset>>> datasets = new HashMap<String, Map<String, Map<String, Dataset>>>();
		Map<String, Map<String, Map<String, Fields>>> fields = new HashMap<String, Map<String, Map<String, Fields>>>();
		TreeSet<DatasetHolder> list = new TreeSet<MetadataEJB.DatasetHolder>();
		Map<String, Definition> defList = new HashMap<String, Definition>();
		Map<String, Set<Dataset>> defDataset = new HashMap<String, Set<Dataset>>();
		List<Dataset> allDatasets = new ArrayList<Dataset>();

		for (String o : structureEJB.getOwners()) {
			Owner owner = Owner.read(o);
			owners.put(o, owner);

			groups.put(o, new HashMap<String, Group>());
			
			datasets.put(o, new HashMap<String, Map<String,Dataset>>());
			fields.put(o, new HashMap<String, Map<String,Fields>>());

			for (String g : structureEJB.getGroups(o)) {
				Group group = Group.read(o, g);
				groups.get(o).put(g, group);

				datasets.get(o).put(g, new HashMap<String, Dataset>());
				fields.get(o).put(g, new HashMap<String, Fields>());

				for (String d : structureEJB.getDatasets(o, g)) {
					try {
						logger.info("Reading " + o + "/" + g + "/" + d);
						Dataset ds = Dataset.read(o, g, d);
						datasets.get(o).get(g).put(d, ds);
						allDatasets.add(ds);
		
						Fields fs = Fields.read(o, g, d);
						if (fs != null) {
							fields.get(o).get(g).put(d, fs);
		
							for (Field f : fs.getFields()) {
								defList.put(f.getDefinition().getShortName(), f.getDefinition());
								if (!defDataset.containsKey(f.getDefinition().getShortName()))
									defDataset.put(f.getDefinition().getShortName(), new TreeSet<Dataset>());
								defDataset.get(f.getDefinition().getShortName()).add(ds);
							}
		
							list.add(new DatasetHolder(structureEJB.getTimestamp(o, g, d), ds));
						}
					} catch (Exception e) {
						logger.log(Level.WARNING, e.getMessage());
					}
				}
			}
		}

		this.owners = owners;
		this.groups = groups;
		this.datasets = datasets;
		this.fields = fields;
		this.list = list;
		this.defList = defList;
		this.defDataset = defDataset;
		
		this.allDatasets = allDatasets;
		Collections.sort(allDatasets);
		
		allDefinitions = new ArrayList<Definition>(defList.values());
		Collections.sort(allDefinitions);
	}

	public List<Owner> getOwners() {
		if (owners != null)
			return new ArrayList<Owner>(owners.values());
		return null;
	}

	public Owner getOwner(String owner) {
		if (owners != null && owners.containsKey(owner))
			return owners.get(owner);
		return null;
	}

	public List<Group> getGroups(String owner) {
		if (groups != null && groups.containsKey(owner))
			return new ArrayList<Group>(groups.get(owner).values());
		return null;
	}

	public Group getGroup(String owner, String group) {
		if (groups != null && groups.containsKey(owner) && groups.get(owner).containsKey(group))
			return groups.get(owner).get(group);
		return null;
	}

	public List<Dataset> getDatasets(String owner, String group) {
		// if (datasets != null && datasets.containsKey(owner)
		// && datasets.get(owner).containsKey(group))
		try {
			return new ArrayList<Dataset>(datasets.get(owner).get(group).values());
		} catch (Exception e) {
			return null;
		}
		// return null;
	}

	public Dataset getDataset(String owner, String group, String dataset) {
		if (datasets != null && datasets.containsKey(owner) && datasets.get(owner).containsKey(group)
				&& datasets.get(owner).get(group).containsKey(dataset))
			return datasets.get(owner).get(group).get(dataset);
		return null;
	}

	public Fields getFields(String owner, String group, String dataset) {
		if (fields != null && fields.containsKey(owner) && fields.get(owner).containsKey(group)
				&& fields.get(owner).get(group).containsKey(dataset))
			return fields.get(owner).get(group).get(dataset);
		return null;
	}

	public List<DatasetHolder> getLast(int number) {
		if (list == null)
			return null;

		ArrayList<DatasetHolder> result = new ArrayList<MetadataEJB.DatasetHolder>();
		Iterator<DatasetHolder> iterator = list.descendingIterator();
		for (int i = 0; i < Math.min(number, list.size()); i++)
			result.add(iterator.next());
		return result;
	}

	public List<Definition> getDefinitions() {
		if (defList != null)
			return new ArrayList<Definition>(defList.values());
		return null;
	}

	public Definition getDefinition(String shortName) {
		if (defList != null && defList.containsKey(shortName))
			return defList.get(shortName);
		return null;
	}

	public List<Dataset> getDefinitionUsage(String shortName) {
		if (defDataset != null && defDataset.containsKey(shortName))
			return new ArrayList<Dataset>(defDataset.get(shortName));
		return null;
	}
	
	public List<Dataset> getAllDatasets() {
		return allDatasets;
	}

	public class DatasetHolder implements Comparable<DatasetHolder> {
		private Long timestamp;
		private Dataset dataset;

		public DatasetHolder(Long timestamp, Dataset dataset) {
			super();

			this.timestamp = timestamp;
			this.dataset = dataset;
		}

		public Date getTimestamp() {
			return new Date(timestamp);
		}

		public Dataset getDataset() {
			return dataset;
		}

		@Override
		public int compareTo(DatasetHolder other) {
			return this.timestamp.compareTo(other.timestamp);
		}
	}
}
