package no.difi.datahotel.logic;

import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.util.CSVReader;
import no.difi.datahotel.util.Filesystem;
import no.difi.datahotel.util.Timestamp;
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

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static no.difi.datahotel.util.Filesystem.*;

@Component("index")
public class IndexBean {

    public static Version version = Version.LUCENE_34;
    @SuppressWarnings("rawtypes")
    public static StandardAnalyzer analyzer = new StandardAnalyzer(version, new HashSet());

    @Autowired
    private FieldBean fieldBean;

    private CSVReader csvReaderFactory = new CSVReader();

    public void delete(String location) {
        Filesystem.delete(FOLDER_CACHE_INDEX, location);
    }

    @SuppressWarnings("rawtypes")
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

            StandardAnalyzer analyzer = new StandardAnalyzer(version, new HashSet());

            IndexWriterConfig writerConfig = new IndexWriterConfig(version, analyzer);
            IndexWriter writer = new IndexWriter(dir, writerConfig);

            writer.deleteAll();

            CSVReader csv = csvReaderFactory.open(filename);
            while (csv.hasNext()) {
                try {
                    i++;
                    Map<String, String> line = csv.getNextLine();
                    Document doc = new Document();
                    String searchable = "";
                    for (FieldLight f : fieldBean.getFields(metadata)) {
                        String value = line.get(f.getShortName());

                        if (value == null)
                            logger.info("Field not found: " + f.getShortName());

                        // TODO if (f.getGroupable())
                        if (value.matches("[0-9.,]+"))
                            doc.add(new Field(f.getShortName(), value, Store.YES, Index.NOT_ANALYZED_NO_NORMS));
                        else
                            doc.add(new Field(f.getShortName(), value, Store.YES, Index.ANALYZED));

                        if (f.getSearchable())
                            searchable += " " + line.get(f.getShortName());
                    }

                    if (!searchable.trim().isEmpty())
                        doc.add(new Field("searchable", searchable.trim(), Store.NO, Index.ANALYZED));

                    writer.addDocument(doc);
                } catch (Exception e) {
                    logger.info("[" + e.getClass().getSimpleName() + (e.getStackTrace().length > 0 ? "][" + e.getStackTrace()[0].getFileName() + ":" + e.getStackTrace()[0].getLineNumber() : "") + "] Unable to index line " + i + ". (" + String.valueOf(e.getMessage())
                            + ")");
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
