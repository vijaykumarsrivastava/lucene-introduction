package com.vijay.lucene.simple.sample;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * This is simple sample for in memory directory. This is maven standalone
 * application. 
 * 
 * Lucene Version: 5.2.1
 * 
 * Some more details: Here, document contains "Bharat is democratic country."
 * string and searching for "democratic" word.
 * 
 * @author vijay
 * 
 */
public class Sample2 {

	private static final String dataOrDocument = "Bharat is democratic country.";
	private static final String queryString = "democratic";
	private static final String fieldname = "contain";

	public static void main(String[] args) throws IOException, ParseException {

		Analyzer analyzer = new StandardAnalyzer();

		// Store the index in memory:
		Directory directory = new RAMDirectory();
		// To store an index on disk, use this instead:
		// Directory directory = FSDirectory.open("/tmp/directoryName");

		// Add analyzer into index writer.
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// Open writer with given directory and configuration.
		IndexWriter writer = new IndexWriter(directory, config);

		// Prepare document.
		Document doc = new Document();
		doc.add(new Field(fieldname, dataOrDocument, TextField.TYPE_STORED));

		// Write document into memory.
		writer.addDocument(doc);
		// close write stream.
		writer.close();

		// Open reader in given directory.
		DirectoryReader reader = DirectoryReader.open(directory);
		// Create the searcher to document.
		IndexSearcher isearcher = new IndexSearcher(reader);

		// Create parser for given fieldName and analyzer.
		QueryParser parser = new QueryParser(fieldname, analyzer);

		// parse the given query.
		Query query = parser.parse(queryString);
		// fire the query.
		ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;

		if (hits.length == 0) {
			System.out.println("No document found.");
		} else {
			System.out.println("Document found:");
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				System.out.println(hitDoc.get("fieldname"));
			}
		}
		// close read stream.
		reader.close();
		// close directory.
		directory.close();
	}
}
