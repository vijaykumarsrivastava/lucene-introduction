package com.vijay.lucene.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * 
 * @author vijay
 *
 */
public class Main {

	public static void main(String[] args) {
		try {
			final Path docDir = Paths.get("."); //current folder
			Directory dir = new RAMDirectory();
			Analyzer analyzer = new StandardAnalyzer();
			Writer writer = new Writer();
			writer.write(dir, analyzer, docDir);
			Reader reader = new Reader();
			reader.read(dir, analyzer, docDir);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
}
