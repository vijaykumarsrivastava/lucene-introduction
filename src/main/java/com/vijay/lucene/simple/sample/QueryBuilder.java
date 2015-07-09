package com.vijay.lucene.simple.sample;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class QueryBuilder {

	public Query buildQuery(final Analyzer analyzer, final String fieldName, final String searchString) throws ParseException {
		QueryParser parser = new QueryParser(fieldName, analyzer);
		Query query = parser.parse(searchString);
		return query;
	}
}
