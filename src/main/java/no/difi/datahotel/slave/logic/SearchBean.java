package no.difi.datahotel.slave.logic;

import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.Result;
import no.difi.datahotel.util.Filesystem;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
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
        Result result = new Result();
        result.setPage(page);

        // StringBuilder query = new StringBuilder();
        BooleanQuery booleanQuery = new BooleanQuery();
        IndexSearcher searcher = searchers.get(metadata.getLocation());
        if (searcher != null) {
            try {
                // System.out.println("---");

                if (lookup != null)
                    for (String key : lookup.keySet()) {
                        addClause(booleanQuery, key, lookup.get(key));
                        // query.append(query.length() == 0 ? "" : " AND ").append("+").append(key).append(":").append(lookup.get(key));
                    }
                if (q != null && !q.equals("")) {
                    addClause(booleanQuery, "searchable", q);
                    // query.append(query.length() == 0 ? "" : " AND ").append(q);
                }


                /* System.out.println("Parsed: " + parser.parse(query.toString()));
                if (parser.parse(query.toString()) instanceof BooleanQuery)
                    for (BooleanClause c : (BooleanQuery) parser.parse(query.toString()))
                        System.out.println(c.getQuery() + " - " + c.getQuery().getClass().getSimpleName()); */

                /* System.out.println("Made:   " + booleanQuery);
                for (BooleanClause c : booleanQuery)
                    System.out.println(c.getQuery() + " - " + c.getQuery().getClass().getSimpleName()); */


                TopDocs docs = searcher.search(booleanQuery, num * page);
                // TopDocs docs = searcher.search(parser.parse(query.toString()), num * page);
                List<Map<String, String>> rdocs = convert(searcher, docs);

				result.setEntries((rdocs.size() < num * (page - 1)) ? new ArrayList<Map<String, String>>() : rdocs.subList(
						num * (page - 1), rdocs.size()));
				result.setPosts(docs.totalHits);
                // } catch (ParseException e) {
                // throw new Exception("Unable to parse query.");
            } catch (Exception e) {
                // metadata.getLogger().warning("Error in search: " + query.toString() + " - Reason: " + e.getClass().getSimpleName() + " - " + e.getStackTrace()[0].toString());
                metadata.getLogger().warning("Error in search: " + booleanQuery.toString() + " - Reason: " + e.getClass().getSimpleName() + " - " + e.getStackTrace()[0].toString());
            }
		}

		return result;
	}

    private void addClause(BooleanQuery booleanQuery, String key, String value) throws IOException {
        addClauseRaw(booleanQuery, key.trim(), value.trim().toLowerCase());
    }

    private void addClauseRaw(BooleanQuery booleanQuery, String key, String value) throws IOException {
        String query = value;
        String rest = null;

        if (query.contains(" ") || query.contains("\"")) {
            if (query.startsWith("\"")) {
                if (query.substring(2).contains("\"")) {
                    rest = query.substring(query.substring(1).indexOf("\"") + 2).trim();
                    query = query.substring(0, query.substring(1).indexOf("\"") + 2);
                }

                if (!query.contains(" "))
                    query = query.replace("\"", "");
            } else {
                rest = query.substring(query.indexOf(" ") + 1).trim();
                query = query.substring(0, query.indexOf(" "));
            }
        }

        if (query.indexOf("*") == query.length() - 1) {
            booleanQuery.add(new BooleanClause(new PrefixQuery(new Term(key, query.replace("*", ""))), BooleanClause.Occur.MUST));
        } else if (query.contains("*")) {
            booleanQuery.add(new BooleanClause(new WildcardQuery(new Term(key, query)), BooleanClause.Occur.MUST));
        } else if (query.startsWith("\"")) {
            PhraseQuery phraseQuery = new PhraseQuery();
            int i = 0;
            for (String sq : query.replace("\"", "").split(" "))
                phraseQuery.add(new Term(key, sq), i++);
            booleanQuery.add(new BooleanClause(phraseQuery, BooleanClause.Occur.MUST));
        } else {
            booleanQuery.add(new BooleanClause(new TermQuery(new Term(key, query)), BooleanClause.Occur.MUST));
        }

        if (rest != null && !rest.equals(""))
            addClauseRaw(booleanQuery, key, rest);
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
