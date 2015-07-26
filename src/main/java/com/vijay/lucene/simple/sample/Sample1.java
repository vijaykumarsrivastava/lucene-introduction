package com.vijay.lucene.simple.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * Index the current directory in to main memory and search 'Reader' word.
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
			
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			indexWriterConfig.setOpenMode(OpenMode.CREATE);
			IndexWriter indexWriter = new IndexWriter(dir, indexWriterConfig);
			indexDocs(indexWriter, docDir);
			indexWriter.close();
			
			// make query
			String fieldName = "contents";
			String searchString = "Reader";
			QueryParser parser = new QueryParser(fieldName, analyzer);
			Query query = parser.parse(searchString);

			IndexReader reader = DirectoryReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(query, 100);
			ScoreDoc[] hits = docs.scoreDocs;
			int numTotalHits = docs.totalHits;
			System.out.println(numTotalHits + " total matching documents");
			for (ScoreDoc scoreDoc : hits) {
				Document doc = searcher.doc(scoreDoc.doc);
				System.out.println(doc);
			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void indexDocs(final IndexWriter indexWriter, final Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					indexDoc(indexWriter, file, attrs.lastModifiedTime().toMillis());
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(indexWriter, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	private static void indexDoc(final IndexWriter indexWriter, final Path file, long lastModified)
			throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			Document doc = new Document();
			Field pathField = new StringField("path", file.toString(),
					Field.Store.YES);
			doc.add(pathField);
			doc.add(new LongField("modified", lastModified, Field.Store.NO));
			doc.add(new TextField("contents", new BufferedReader(
					new InputStreamReader(stream, StandardCharsets.UTF_8))));
			if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
				System.out.println("adding " + file);
				indexWriter.addDocument(doc);
			} else {
				System.out.println("updating " + file);
				indexWriter.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}

}
