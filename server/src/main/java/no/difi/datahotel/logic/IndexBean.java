package no.difi.datahotel.logic;

import static no.difi.datahotel.util.Filesystem.FILE_DATASET;
import static no.difi.datahotel.util.Filesystem.FOLDER_CACHE_INDEX;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.Filesystem;
import no.difi.datahotel.util.Timestamp;
import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("index")
public class IndexBean {

	private CSVParserFactory csvParserFactory = new CSVParserFactory();
	
	@Autowired
	private FieldBean fieldBean;
	
	public void delete(String location) {
		Filesystem.delete(FOLDER_CACHE_INDEX, location);
	}

	public void update(Metadata metadata) {
		Logger logger = metadata.getLogger();
		Timestamp ts = new Timestamp(FOLDER_CACHE_INDEX, metadata.getLocation(), "timestamp");
		
		if (metadata.getUpdated() == ts.getTimestamp()) {
			logger.info("Index up to date.");
			return;
		}
		
		logger.info("Building index.");
		
		long i = 0;

		try {
			File filename = Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(), FILE_DATASET);

			Directory dir = FSDirectory.open(Filesystem.getFolder(FOLDER_CACHE_INDEX, metadata.getLocation()));
			
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
					for (FieldLight f : fieldBean.getFields(metadata)) {
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
					logger.info("[" + e.getClass().getSimpleName() + "] Unable to index line " + i + ". (" + String.valueOf(e.getMessage()) + ")");
				}
				
				if (i % 10000 == 0)
					logger.info("Document " + i);
			}

			writer.optimize();
			writer.commit();
			writer.close();
			dir.close();
			
			ts.setTimestamp(metadata.getUpdated());
			ts.save();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public void setFielBean(FieldBean fieldBean) {
		this.fieldBean = fieldBean;
	}
}
