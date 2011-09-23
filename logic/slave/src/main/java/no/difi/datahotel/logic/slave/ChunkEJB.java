package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.FOLDER_CHUNK;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SHARED;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;
import no.difi.datahotel.util.csv.CSVWriter;
import no.difi.datahotel.util.shared.Filesystem;

@Stateless
public class ChunkEJB {

	private static Logger logger = Logger.getLogger(ChunkEJB.class.getSimpleName());

	private int size = 100;

	public void update(String owner, String group, String dataset) {
		try {
			String datasetTmp = dataset + "-tmp." + System.currentTimeMillis();

			File source = Filesystem.getFileF(FOLDER_SHARED, owner, group, dataset, "dataset.csv");
			CSVParser parser = CSVParserFactory.getCSVParser(source);

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

}
