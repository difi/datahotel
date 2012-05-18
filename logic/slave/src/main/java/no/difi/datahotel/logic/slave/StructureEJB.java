package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SHARED;
import static no.difi.datahotel.util.shared.Filesystem.STRUCTURE;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import no.difi.datahotel.util.bridge.Structure;
import no.difi.datahotel.util.shared.Filesystem;

@Singleton
public class StructureEJB {

	private static Logger logger = Logger.getLogger(StructureEJB.class.getSimpleName());

	@EJB
	private ChunkEJB chunkEJB;
	@EJB
	private IndexEJB indexEJB;

	private Map<String, Map<String, Map<String, Long>>> structure = new HashMap<String, Map<String, Map<String, Long>>>();

	private long currentLastModified;

	@Schedule(second = "13,37", minute = "*", hour = "*")
	@PostConstruct
	public void update() {
		// Run only when needed.
		File file = Filesystem.getFileF(FOLDER_SHARED, STRUCTURE);
		if (!file.exists() || file.lastModified() == currentLastModified)
			return;

		// Get the updated structure.
		currentLastModified = file.lastModified();
		Structure ds = Structure.read();
		Map<String, Map<String, Map<String, Long>>> nStructure = ds.getStructure();

		// Prepare new and updated.
		for (String o : nStructure.keySet())
			for (String g : nStructure.get(o).keySet())
				for (String d : nStructure.get(o).get(g).keySet())
					if (!new Long(nStructure.get(o).get(g).get(d)).equals(getTimestamp(o, g, d))) {
						logger.info("Update: " + o + "/" + g + "/" + d);
						try
						{
							chunkEJB.update(o, g, d, getTimestamp(o, g, d));
							indexEJB.update(o, g, d, getTimestamp(o, g, d));
						} catch (Exception e)
						{
							logger.log(Level.WARNING, e.getMessage());
						}
					}

		Map<String, Map<String, Map<String, Long>>> oStructure = structure;
		structure = nStructure;

		// Delete deleted sets.
		for (String o : oStructure.keySet())
			for (String g : oStructure.get(o).keySet())
				for (String d : oStructure.get(o).get(g).keySet())
					if (getTimestamp(o, g, d) == null) {
						logger.info("Delete: " + o + "/" + g + "/" + d);
						indexEJB.delete(o, g, d);
						chunkEJB.delete(o, g, d);
					}
	}

	public Long getTimestamp(String owner, String group, String dataset) {
		try {
			return structure.get(owner).get(group).get(dataset);
		} catch (Exception e) {
			return null;
		}
	}

	public Set<String> getOwners() {
		return structure.keySet();
	}

	public Set<String> getGroups(String owner) {
		if (structure.containsKey(owner))
			return structure.get(owner).keySet();
		return null;
	}

	public Set<String> getDatasets(String owner, String group) {
		if (structure.containsKey(owner) && structure.get(owner).containsKey(group))
			return structure.get(owner).get(group).keySet();
		return null;
	}
}
