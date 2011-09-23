package no.difi.datahotel.logic.slave;

import static no.difi.datahotel.util.shared.Filesystem.FOLDER_INDEX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import no.difi.datahotel.util.shared.Filesystem;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

@Stateless
public class SearchEJB {

	private static QueryParser parser = new QueryParser(Version.LUCENE_33, "searchable", new StandardAnalyzer(Version.LUCENE_33));

	public ArrayList<Map<String, String>> find(String owner, String group, String dataset, String q) throws Exception {
		Directory dir = FSDirectory.open(Filesystem.getFolderPathF(FOLDER_INDEX, owner, group, dataset));
		IndexSearcher searcher = new IndexSearcher(dir);

		TopDocs docs = searcher.search(parser.parse(q), 10);
		ArrayList<Map<String, String>> result = convert(searcher, docs);

		searcher.close();
		dir.close();

		return result;
	}
	
	public ArrayList<Map<String, String>> lookup(String owner, String group, String dataset, Map<String, String> query) throws Exception {
		Directory dir = FSDirectory.open(Filesystem.getFolderF(FOLDER_INDEX, owner, group, dataset));
		IndexSearcher searcher = new IndexSearcher(dir);
		
		String[] values = new String[query.size()];
		String[] keys = new String[query.size()];
		Occur[] occurs = new BooleanClause.Occur[query.size()];
		
		for (int i = 0; i < query.size(); i++)
		{
			String key = (String) query.keySet().toArray()[i];
			values[i] = query.get(key);
			keys[i] = key;
			occurs[i] = Occur.MUST;
		}
		
		Query q = MultiFieldQueryParser.parse(Version.LUCENE_33, values, keys, occurs, new StandardAnalyzer(Version.LUCENE_33));
		
		TopDocs docs = searcher.search(q, 100);
		ArrayList<Map<String, String>> result = convert(searcher, docs);

		searcher.close();
		dir.close();

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
