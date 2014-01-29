package no.difi.datahotel.slave.logic;

import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.Result;
import no.difi.datahotel.util.Filesystem;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static no.difi.datahotel.util.Filesystem.FOLDER_CACHE_INDEX;

@Component("search")
public class SearchBean {

	private int num = 100;
    private QueryParser parser = new QueryParser(IndexBean.version, "searchable", IndexBean.analyzer);

    private Map<String, Directory> directories = new HashMap<String, Directory>();
	private Map<String, IndexSearcher> searchers = new HashMap<String, IndexSearcher>();

	public void update(Metadata metadata) {
		try {
			Directory oldDirectory = directories.get(metadata.getLocation());
			IndexSearcher oldSearcher = searchers.get(metadata.getLocation());

			Directory newDirectory = FSDirectory.open(Filesystem.getFolderPath(FOLDER_CACHE_INDEX, metadata.getLocation()));
			IndexSearcher newSearcher = new IndexSearcher(newDirectory);

			directories.put(metadata.getLocation(), newDirectory);
			searchers.put(metadata.getLocation(), newSearcher);

			if (oldSearcher != null)
				oldSearcher.close();
			if (oldDirectory != null)
				oldDirectory.close();
		} catch (Exception e) {
			metadata.getLogger().log(Level.WARNING, "Unable to load searcher.", e);
		}
	}

	public Result find(Metadata metadata, String q, Map<String, String> lookup, int page) {
        // BooleanQuery booleanQuery = new BooleanQuery();
        StringBuilder query = new StringBuilder();
        if (lookup != null)
            for (String key : lookup.keySet()) {
                // booleanQuery.add(new BooleanClause(new WildcardQuery(new Term(key, lookup.get(key))), BooleanClause.Occur.MUST));
                query.append(query.length() == 0 ? "" : " AND ").append("+").append(key).append(":").append(lookup.get(key));
            }
        if (q != null && !q.equals("")) {
            // booleanQuery.add(new BooleanClause(new WildcardQuery(new Term("searchable", q.toLowerCase())), BooleanClause.Occur.SHOULD));
            // booleanQuery.add(new BooleanClause(new WildcardQuery(new Term(q.toLowerCase())), BooleanClause.Occur.SHOULD));
            query.append(query.length() == 0 ? "" : " AND ").append(q);
        }

        Result result = new Result();
		result.setPage(page);

		IndexSearcher searcher = searchers.get(metadata.getLocation());
		if (searcher != null) {
			try {
                // TopDocs docs = searcher.search(booleanQuery, num * page);

                // System.out.println(query);
                // System.out.println(parser.parse(query.toString()));
                // System.out.println(booleanQuery);

                TopDocs docs = searcher.search(parser.parse(query.toString()), num * page);
                List<Map<String, String>> rdocs = convert(searcher, docs);

				result.setEntries((rdocs.size() < num * (page - 1)) ? new ArrayList<Map<String, String>>() : rdocs.subList(
						num * (page - 1), rdocs.size()));
				result.setPosts(docs.totalHits);
                // } catch (ParseException e) {
                // throw new Exception("Unable to parse query.");
            } catch (Exception e) {
                metadata.getLogger().warning("Error in search: " + query.toString() + " - Reason: " + e.getClass().getSimpleName());
                // metadata.getLogger().warning("Error in search: " + booleanQuery.toString() + " - Reason: " + e.getClass().getSimpleName() + " - " + e.getStackTrace()[0].toString());
            }
		}

		return result;
	}

	private ArrayList<Map<String, String>> convert(IndexSearcher searcher, TopDocs docs) throws IOException {
		ArrayList<Map<String, String>> results = new ArrayList<Map<String, String>>();
		for (ScoreDoc doc : docs.scoreDocs) {
			HashMap<String, String> result = new HashMap<String, String>();
			for (Fieldable fi : searcher.doc(doc.doc).getFields())
				result.put(fi.name(), fi.stringValue());
			results.add(result);
		}

		return results;
	}
}
