package com.vijay.lucene.simple.sample;

import java.io.IOException;
import java.util.Scanner;

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
 * @author vijay
 * 
 */
public class Sample2_2 {

	private static final String FIELD_NAME = "contain";
	private static final int SEARCH_PAGE_SIZE = 10;

	public static void main(String[] args) throws IOException, ParseException {

		String readLine = null;
		
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
		Scanner docScanner = new Scanner(System.in);
		System.out.println("Please enter file text.");
		while((readLine = docScanner.nextLine()) != null) {
			if(readLine.isEmpty()) {
				break;
			}
			Document doc = new Document();
			doc.add(new Field(FIELD_NAME, readLine, TextField.TYPE_STORED));
			// Write document into memory.
			writer.addDocument(doc);
		}
		
		// close write stream.
		writer.close();

		// Open reader in given directory.
		DirectoryReader reader = DirectoryReader.open(directory);
		// Create the searcher to document.
		IndexSearcher isearcher = new IndexSearcher(reader);

		// Create parser for given fieldName and analyzer.
		QueryParser parser = new QueryParser(FIELD_NAME, analyzer);

		System.out.println("Please enter search text(q for quit):");
		while (docScanner.hasNext()) {
			readLine = docScanner.nextLine();
			if(readLine.isEmpty() || readLine.equalsIgnoreCase("q")) {
				break;
			}
			// parse the given query.
			Query query = parser.parse(readLine);
			// fire the query.
			ScoreDoc[] hits = isearcher.search(query, SEARCH_PAGE_SIZE).scoreDocs;

			if (hits.length == 0) {
				System.out.println("No document found.");
			} else {
				System.out.println("Document found:");
				for (int i = 0; i < hits.length; i++) {
					Document hitDoc = isearcher.doc(hits[i].doc);
					System.out.println(hitDoc.get(FIELD_NAME));
				}
			}
		}
		docScanner.close();
		
		// close read stream.
		reader.close();
		// close directory.
		directory.close();
	}
}
