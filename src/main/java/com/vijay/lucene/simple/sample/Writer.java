package com.vijay.lucene.simple.sample;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;

/**
 * 
 * @author vijay
 *
 */
public class Writer implements Closeable {

	private final IndexWriter indexWriter;
	
	public Writer(final Directory dir, final Analyzer analyzer,
			final Path docDir) throws IOException {
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		this.indexWriter = new IndexWriter(dir, iwc);
	}
	
	public void indexDocs(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					indexDoc(file, attrs.lastModifiedTime().toMillis());
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	private void indexDoc(Path file, long lastModified)
			throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			Document doc = new Document();
			Field pathField = new StringField("path", file.toString(),
					Field.Store.YES);
			doc.add(pathField);
			doc.add(new LongField("modified", lastModified, Field.Store.NO));
			doc.add(new TextField("contents", new BufferedReader(
					new InputStreamReader(stream, StandardCharsets.UTF_8))));
			if (this.indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
				System.out.println("adding " + file);
				this.indexWriter.addDocument(doc);
			} else {
				System.out.println("updating " + file);
				this.indexWriter.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}

	@Override
	public void close() throws IOException {
		this.indexWriter.close();
	}
}
