package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.FOLDER_CHUNK;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SHARED;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;

import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;
import no.difi.datahotel.util.csv.CSVWriter;
import no.difi.datahotel.util.shared.Filesystem;
import no.difi.datahotel.util.shared.Timestamp;

@Singleton
public class ChunkEJB {

	private static Logger logger = Logger.getLogger(ChunkEJB.class.getSimpleName());

	private Map<String, Map<String, Map<String, Long>>> posts = new HashMap<String, Map<String, Map<String, Long>>>();
	private Map<String, Map<String, Map<String, Long>>> pages = new HashMap<String, Map<String, Map<String, Long>>>();

	private int size = 100;

	public File getFullDataset(String owner, String group, String dataset) {
		return Filesystem.getFileF(FOLDER_SHARED, owner, group, dataset, "dataset.csv");
	}

	public void update(String owner, String group, String dataset, long timestamp) {
		File tsfile = Filesystem.getFileF(FOLDER_CHUNK, owner, group, dataset, "timestamp");
		if (timestamp == Timestamp.getTimestamp(tsfile))
			return;

		try {
			String datasetTmp = dataset + "-tmp." + System.currentTimeMillis();

			CSVParser parser = CSVParserFactory.getCSVParser(getFullDataset(owner, group, dataset));
			CSVWriter writer = null;

			int number = 1, counter = 0;
			while (parser.hasNext()) {
				counter++;

				if (counter % size == 1) {
					String filename = "dataset-" + number + ".csv";
					writer = new CSVWriter(Filesystem.getFileF(FOLDER_CHUNK, owner, group, datasetTmp, filename));
					writer.writeHeader(parser.getHeaders());
				}

				writer.write(parser.getNextLineArray());

				if (counter % size == 0) {
					writer.close();
					writer = null;
					number++;
				}
			}

			if (writer != null)
				writer.close();

			File goal = Filesystem.getFolderPathF(FOLDER_CHUNK, owner, group, dataset);
			if (goal.exists())
				delete(owner, group, dataset);

			Filesystem.getFolderPathF(FOLDER_CHUNK, owner, group, datasetTmp).renameTo(goal);

			if (!posts.containsKey(owner)) {
				posts.put(owner, new HashMap<String, Map<String, Long>>());
				pages.put(owner, new HashMap<String, Map<String, Long>>());
			}
			if (!posts.get(owner).containsKey(group)) {
				posts.get(owner).put(group, new HashMap<String, Long>());
				pages.get(owner).put(group, new HashMap<String, Long>());
			}
			posts.get(owner).get(group).put(dataset, (long) counter);
			pages.get(owner).get(group).put(dataset, (long) number);

			Timestamp.setTimestamp(tsfile, timestamp);
		} catch (IOException e) {
			// TODO Start sending exceptions.
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public void delete(String owner, String group, String dataset) {
		Filesystem.delete(FOLDER_CHUNK, owner, group, dataset);
	}

	public ArrayList<Map<String, String>> get(String owner, String group, String dataset, int number) {
		File source = Filesystem.getFileF(FOLDER_CHUNK, owner, group, dataset, "dataset-" + number + ".csv");

		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		try {
			CSVParser parser = CSVParserFactory.getCSVParser(source);

			while (parser.hasNext())
				result.add(parser.getNextLine());

			parser.close();

			return result;
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
		return null;
	}

	public Long getPosts(String owner, String group, String dataset) {
		try {
			return posts.get(owner).get(group).get(dataset);
		} catch (Exception e) {
			return 0L;
		}
	}

	public Long getPages(String owner, String group, String dataset) {
		try {
			return pages.get(owner).get(group).get(dataset);
		} catch (Exception e) {
			return 0L;
		}
	}
}
