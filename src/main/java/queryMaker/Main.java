package queryMaker;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

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
    @SuppressWarnings("deprecation")
    private static void executeQuery(IndexSearcher searcher, String queryInput) throws Exception {
        Query query = null;
        if (queryInput.startsWith("title: ")) {
            String queryField = "title";
            String queryString = queryInput.replaceFirst("title: ", "");
            query = new PhraseQuery(1, queryField, queryString.split(" "));
        } else if (queryInput.startsWith("body: ")){
            String queryField = "body";
            String queryString = queryInput.replaceFirst("body: ", "");
            QueryParser queryParser = new QueryParser(queryField, new WhitespaceAnalyzer());
            query = queryParser.parse(queryString);
        } else {
            throw new Exception();
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
        String anotherQuery = null;
        do {
            System.out.println("Enter query:");
            String queryString = scanner.nextLine();
            try { executeQuery(searcher, queryString); } 
            catch (Exception e) { System.out.println("query input must be like <field>: <query>"); }
            System.out.println("Make another query? (y/n)");
            anotherQuery = scanner.nextLine();
        } while (anotherQuery.equals("y"));
        System.out.println("Bye bye");
        directory.close();
        scanner.close();
    }
}