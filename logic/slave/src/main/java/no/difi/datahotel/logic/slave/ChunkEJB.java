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

import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;
import no.difi.datahotel.util.csv.CSVWriter;
import no.difi.datahotel.util.shared.Filesystem;
import no.difi.datahotel.util.shared.Timestamp;

@Singleton
public class ChunkEJB {

	private static Logger logger = Logger.getLogger(ChunkEJB.class.getSimpleName());

	private Map<String, Long> posts = new HashMap<String, Long>();
	private Map<String, Long> pages = new HashMap<String, Long>();

	private int size = 100;

	public File getFullDataset(Metadata metadata) {
		return Filesystem.getFileF(FOLDER_SHARED, metadata.getLocation(), Filesystem.DATASET_DATA);
	}
	
	public void update(Metadata metadata) {
		File tsfile = Filesystem.getFileF(FOLDER_CHUNK, metadata.getLocation(), "timestamp");
		if (metadata.getUpdated() == Timestamp.getTimestamp(tsfile)) {
			logger.info("[" + metadata.getLocation() + "] Chunk up to date.");
			return;
		}

		logger.info("[" + metadata.getLocation() + "] Building chunk.");

		try {
			String locationTmp = metadata.getLocation() + "-tmp." + System.currentTimeMillis();

			CSVParser parser = CSVParserFactory.getCSVParser(getFullDataset(metadata));
			CSVWriter writer = null;

			long number = 0, counter = 0;
			while (parser.hasNext()) {
				counter++;

				if (counter % size == 1) {
					number++;
					String filename = "dataset-" + number + ".csv";
					writer = new CSVWriter(Filesystem.getFileF(FOLDER_CHUNK, locationTmp, filename));
					writer.writeHeader(parser.getHeaders());
				}

				writer.write(parser.getNextLineArray());

				if (counter % size == 0) {
					writer.close();
					writer = null;
				}
			}
			
			if (writer != null)
				writer.close();

			File goal = Filesystem.getFolderPathF(FOLDER_CHUNK, metadata.getLocation());
			if (goal.exists())
				Filesystem.delete(FOLDER_CHUNK, metadata.getLocation());

			Filesystem.getFolderPathF(FOLDER_CHUNK, locationTmp).renameTo(goal);

			posts.put(metadata.getLocation(), counter);
			pages.put(metadata.getLocation(), number);

			Timestamp.setTimestamp(tsfile, metadata.getUpdated());
		} catch (Exception e) {
			// TODO Start sending exceptions.
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public ArrayList<Map<String, String>> get(String location, int number) {
		File source = Filesystem.getFileF(FOLDER_CHUNK, location, "dataset-" + number + ".csv");

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

	public Long getPosts(String location) {
		return posts.containsKey(location) ? posts.get(location) : 0;
	}

	public Long getPages(String location) {
		return pages.containsKey(location) ? pages.get(location) : 0;
	}
}
