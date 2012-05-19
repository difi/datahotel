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

	@EJB
	private FieldEJB fieldEJB;
	
	public void delete(String owner, String group, String dataset) {
		Filesystem.delete(FOLDER_INDEX, owner, group, dataset);
	}

	public void update(String owner, String group, String dataset, long timestamp) {
		File tsfile = Filesystem.getFileF(FOLDER_INDEX, owner, group, dataset, "timestamp");
		if (timestamp == Timestamp.getTimestamp(tsfile)) {
			logger.info("[" + owner + "/" + group +"/" + dataset + "] Index up to date.");
			return;
		}
		
		logger.info("[" + owner + "/" + group +"/" + dataset + "] Building index.");
		
		try {
			File filename = Filesystem.getFileF(FOLDER_SHARED, owner, group, dataset, DATASET_DATA);

			Directory dir = FSDirectory.open(Filesystem.getFolderF(FOLDER_INDEX, owner, group, dataset));
			
			IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_33, new StandardAnalyzer(Version.LUCENE_33));
			IndexWriter writer = new IndexWriter(dir, writerConfig);

			writer.deleteAll();

			CSVParser csv = CSVParserFactory.getCSVParser(filename);
			long i = 0;
			while (csv.hasNext()) {
				i++;
				Map<String, String> line = csv.getNextLine();
				Document doc = new Document();
				String searchable = "";
				for (no.difi.datahotel.util.bridge.Field f : fieldEJB.getFields(owner, group, dataset)) {
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
				
				if (i % 10000 == 0)
					logger.info("[" + owner + "/" + group +"/" + dataset + "] Document " + i);
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
