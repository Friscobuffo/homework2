package queryMaker;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Main {
    private static ImmutablePair<String, String> getQueryFields(String queryInput) {
        int separator = queryInput.indexOf(":");
        String queryField = queryInput.substring(0, separator);
        String queryString = queryInput.substring(separator+2, queryInput.length());
        return new ImmutablePair<>(queryField, queryString);
    }

    @SuppressWarnings("deprecation")
    private static void executeQuery(IndexSearcher searcher, String queryInput) throws Exception {
        ImmutablePair<String, String> queryFields = getQueryFields(queryInput);
        String queryField = queryFields.left;
        String queryString = queryFields.right;
        Query query = null;
        if (queryField.equals("title")) {
            query = new PhraseQuery(1, queryField, queryString.split(" "));
        } else {
            QueryParser queryParser = new QueryParser(queryField, new WhitespaceAnalyzer());
            query = queryParser.parse(queryString);
        }
        TopDocs hits = searcher.search(query, 5);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("doc"+scoreDoc.doc + ": "+ doc.get("title") + " (" + scoreDoc.score +")");
        }
    }

    public static void main(String[] args) throws Exception {
        Path path = Paths.get("/home/giordy/Documents/homework2/index");
        Directory directory = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter query:");
        String queryString = scanner.nextLine();
        executeQuery(searcher, queryString);

        directory.close();
        scanner.close();
    }
}