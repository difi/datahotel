package no.difi.datahotel.slave.logic;

import static no.difi.datahotel.util.Filesystem.FOLDER_CACHE_CHUNK;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;

import no.difi.datahotel.model.Result;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.Filesystem;
import no.difi.datahotel.util.Timestamp;
import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;
import no.difi.datahotel.util.csv.CSVWriter;

@Singleton
public class ChunkEJB {

	private Map<String, Long> posts = new HashMap<String, Long>();

	private int size = 100;

	public File getFullDataset(Metadata metadata) {
		return Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), Filesystem.FILE_DATASET);
	}

	public File getMetadata(Metadata metadata) {
		return Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), Filesystem.FILE_METADATA);
	}

	public File getFields(Metadata metadata) {
		return Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), Filesystem.FILE_FIELDS);
	}

	public void update(Metadata metadata) {
		Logger logger = metadata.getLogger();
		Timestamp ts = new Timestamp(FOLDER_CACHE_CHUNK, metadata.getLocation(), "timestamp");
		
		if (metadata.getUpdated() == ts.getTimestamp()) {
			posts.put(metadata.getLocation(), ts.getLong("posts"));
			
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

			ts.setTimestamp(metadata.getUpdated());
			ts.set("posts", counter);
			ts.save();
		} catch (Exception e) {
			// TODO Start sending exceptions.
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public Result get(Metadata metadata, long page) {
		Logger logger = metadata.getLogger();
		
		try {
			File source = Filesystem.getFile(FOLDER_CACHE_CHUNK, metadata.getLocation(), "dataset-" + page + ".csv");

			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			CSVParser parser = CSVParserFactory.getCSVParser(source);

			while (parser.hasNext())
				data.add(parser.getNextLine());
			parser.close();

			Result result = new Result(data);
			result.setPage(page);
			result.setPosts(posts.get(metadata.getLocation()));
			return result;
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
		}
		return null;
	}

	public Long getPosts(String location) {
		return posts.containsKey(location) ? posts.get(location) : 0;
	}
}
