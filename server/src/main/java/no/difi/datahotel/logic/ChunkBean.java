package no.difi.datahotel.logic;

import static no.difi.datahotel.util.Filesystem.FOLDER_CACHE_CHUNK;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.Result;
import no.difi.datahotel.util.Filesystem;
import no.difi.datahotel.util.Timestamp;
import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;
import no.difi.datahotel.util.csv.CSVWriter;

@Component("chunk")
public class ChunkBean {
	
	private Map<String, Long> posts = new HashMap<String, Long>();

	private int size = 100;

	// TODO: Move
	public File getFullDataset(Metadata metadata) {
		return Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), Filesystem.FILE_DATASET);
	}

	// TODO: Move
	public File getMetadata(Metadata metadata) {
		return Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), Filesystem.FILE_METADATA);
	}

	// TODO: Move
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

		Result result = new Result();
		result.setPage(page);
		result.setPosts(posts.containsKey(metadata.getLocation()) ? posts.get(metadata.getLocation()) : 0);

		if (page <= result.getPages()) {
			try {
				File source = Filesystem.getFile(FOLDER_CACHE_CHUNK, metadata.getLocation(), "dataset-" + page + ".csv");
	
				if (source.isFile()) {
					List<Map<String, String>> data = new ArrayList<Map<String, String>>();
					CSVParser parser = CSVParserFactory.getCSVParser(source);
	
					while (parser.hasNext())
						data.add(parser.getNextLine());
					parser.close();
	
					result.setEntries(data);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, e.getMessage());
			}
		}

		return result;
	}
}
