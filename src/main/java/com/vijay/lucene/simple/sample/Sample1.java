package com.vijay.lucene.simple.sample;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * 
 * @author vijay
 *
 */
public class Sample1 {

	public static void main(String[] args) {

		try {
			// Index files.
			final Path docDir = Paths.get("."); // search in current folder
			Directory dir = new RAMDirectory();
			Analyzer analyzer = new StandardAnalyzer();
			Writer writer = new Writer(dir, analyzer, docDir);
			writer.indexDocs(docDir);
			writer.close();
			
			// Open connection for read
			Reader reader = new Reader(dir, analyzer, docDir);

			// make query
			String fieldName = "contents";
			String searchString = "Reader";
			QueryBuilder builder = new QueryBuilder();
			Query query = builder.buildQuery(analyzer, fieldName, searchString);

			// fire query
			reader.executeQuery(query);

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

}
