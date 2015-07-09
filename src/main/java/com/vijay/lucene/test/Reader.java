package com.vijay.lucene.test;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

/**
 * 
 * @author vijay
 *
 */
public class Reader {

	public void read(final Directory dir, final Analyzer analyzer,
			final Path docDir) throws IOException, ParseException {
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		String field = "contents";
		QueryParser parser = new QueryParser(field, analyzer);
		String line = "Reader"; // TODO
		Query query = parser.parse(line);
		TopDocs docs = searcher.search(query, 100);
		ScoreDoc[] hits = docs.scoreDocs;
		int numTotalHits = docs.totalHits;
		System.out.println(numTotalHits + " total matching documents");
		for(ScoreDoc scoreDoc: hits) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(doc);
		}
	}
}
