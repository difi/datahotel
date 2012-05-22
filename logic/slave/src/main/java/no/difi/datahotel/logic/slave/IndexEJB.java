package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.DATASET_DATA;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_INDEX;
import static no.difi.datahotel.util.shared.Filesystem.FOLDER_SHARED;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;
import no.difi.datahotel.util.shared.Filesystem;
import no.difi.datahotel.util.shared.Timestamp;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

@Stateless
public class IndexEJB {

	private static Logger logger = Logger.getLogger(IndexEJB.class.getSimpleName());
	private CSVParserFactory csvParserFactory = new CSVParserFactory();
	
	@EJB
	private FieldEJB fieldEJB;
	
	public void delete(String owner, String group, String dataset) {
		Filesystem.delete(FOLDER_INDEX, owner, group, dataset);
	}

	public void update(Metadata metadata) {
		update(metadata.getLocation(), metadata.getUpdated());
	}
	
	public void update(String location, long timestamp) {
		File tsfile = Filesystem.getFileF(FOLDER_INDEX, location, "timestamp");
		if (timestamp == Timestamp.getTimestamp(tsfile)) {
			logger.info("[" + location + "] Index up to date.");
			return;
		}
		
		logger.info("[" + location + "] Building index.");
		
		long i = 0;

		try {
			File filename = Filesystem.getFileF(FOLDER_SHARED, location, DATASET_DATA);

			Directory dir = FSDirectory.open(Filesystem.getFolderF(FOLDER_INDEX, location));
			
			IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_33, new StandardAnalyzer(Version.LUCENE_33));
			IndexWriter writer = new IndexWriter(dir, writerConfig);

			writer.deleteAll();

			CSVParser csv = csvParserFactory.get(filename);
			while (csv.hasNext()) {
				try {
					i++;
					Map<String, String> line = csv.getNextLine();
					Document doc = new Document();
					String searchable = "";
					for (no.difi.datahotel.util.bridge.Field f : fieldEJB.getFields(location)) {
						String value = line.get(f.getShortName());
						
						if (value.matches("[0-9.,]+"))
							doc.add(new Field(f.getShortName(), value, Store.YES,  Index.NOT_ANALYZED_NO_NORMS));
						else
							doc.add(new Field(f.getShortName(), value, Store.YES,  Index.ANALYZED));
	
						if (f.getSearchable())
							searchable += " " + line.get(f.getShortName());
					}
	
					if (!searchable.trim().isEmpty())
						doc.add(new Field("searchable", searchable.trim(), Store.NO, Index.ANALYZED));
	
					writer.addDocument(doc);
				} catch (Exception e) {
					logger.info("[" + location + "] [" + e.getClass().getSimpleName() + "] Unable to index line " + i + ". (" + String.valueOf(e.getMessage()) + ")");
				}
				
				if (i % 10000 == 0)
					logger.info("[" + location + "] Document " + i);
			}

			writer.optimize();
			writer.commit();
			writer.close();
			dir.close();
			
			Timestamp.setTimestamp(tsfile, timestamp);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}
}
