package com.vijay.lucene.simple.sample;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
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

	private final IndexSearcher searcher;

	public Reader(final Directory dir, final Analyzer analyzer,
			final Path docDir) throws IOException {
		IndexReader reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
	}

	public void executeQuery(final Query query) throws IOException, ParseException {
		TopDocs docs = searcher.search(query, 100);
		ScoreDoc[] hits = docs.scoreDocs;
		int numTotalHits = docs.totalHits;
		System.out.println(numTotalHits + " total matching documents");
		for (ScoreDoc scoreDoc : hits) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(doc);
		}
	}
}
