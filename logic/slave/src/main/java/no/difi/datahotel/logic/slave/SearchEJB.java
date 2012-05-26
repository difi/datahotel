package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.FOLDER_CACHE_INDEX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.shared.Filesystem;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

@Stateless
public class SearchEJB {

	private static QueryParser parser = new QueryParser(Version.LUCENE_33, "searchable", new StandardAnalyzer(Version.LUCENE_33));

	public List<Map<String, String>> find(Metadata metadata, String q, Map<String, String> lookup, int page) throws Exception {
		int num = 100;

		StringBuilder query = new StringBuilder();
		if (lookup != null)
			for (String key : lookup.keySet())
				query.append(query.length() == 0 ? "" : " AND ").append("+").append(key).append(":").append(lookup.get(key));
		if (q != null && !q.equals(""))
			query.append(query.length() == 0 ? "" : " AND ").append(q);
		
		Directory dir = FSDirectory.open(Filesystem.getFolderPath(FOLDER_CACHE_INDEX, metadata.getLocation()));
		IndexSearcher searcher = new IndexSearcher(dir);

		TopDocs docs = searcher.search(parser.parse(query.toString()), num * page);
		List<Map<String, String>> result = convert(searcher, docs);
		
		searcher.close();
		dir.close();

		return (result.size() < num * (page - 1)) ? new ArrayList<Map<String,String>>() : result.subList(num * (page - 1), result.size());
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
