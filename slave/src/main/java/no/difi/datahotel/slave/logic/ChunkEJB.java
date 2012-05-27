package no.difi.datahotel.slave.logic;

import static no.difi.datahotel.util.shared.Filesystem.FOLDER_CACHE_CHUNK;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SLAVE;

import java.io.File;
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

	private Map<String, Long> posts = new HashMap<String, Long>();
	private Map<String, Long> pages = new HashMap<String, Long>();

	private int size = 100;

	public File getFullDataset(Metadata metadata) {
		return Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), Filesystem.FILE_DATASET);
	}
	
	public void update(Metadata metadata) {
		Logger logger = metadata.getLogger();
		
		File tsfile = Filesystem.getFile(FOLDER_CACHE_CHUNK, metadata.getLocation(), "timestamp");
		if (metadata.getUpdated() == Timestamp.getTimestamp(tsfile)) {
			logger.info("Chunk up to date.");
			return;
		}

		logger.info("Building chunk.");

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
					writer = new CSVWriter(Filesystem.getFile(FOLDER_CACHE_CHUNK, locationTmp, filename));
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

			File goal = Filesystem.getFolderPath(FOLDER_CACHE_CHUNK, metadata.getLocation());
			Filesystem.delete(FOLDER_CACHE_CHUNK, metadata.getLocation());

			Filesystem.getFolderPath(FOLDER_CACHE_CHUNK, locationTmp).renameTo(goal);

			posts.put(metadata.getLocation(), counter);
			pages.put(metadata.getLocation(), number);

			Timestamp.setTimestamp(tsfile, metadata.getUpdated());
		} catch (Exception e) {
			// TODO Start sending exceptions.
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public ArrayList<Map<String, String>> get(Metadata metadata, int number) {
		Logger logger = metadata.getLogger();
		
		File source = Filesystem.getFile(FOLDER_CACHE_CHUNK, metadata.getLocation(), "dataset-" + number + ".csv");

		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		try {
			CSVParser parser = CSVParserFactory.getCSVParser(source);

			while (parser.hasNext())
				result.add(parser.getNextLine());

			parser.close();

			return result;
		} catch (Exception e) {
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
