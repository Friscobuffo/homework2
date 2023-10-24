package Query;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import Analyzer.AnalyzerFactory;

public class QueryHandler {
    Path path = null;
    Directory directory = null;
    IndexReader reader = null;
    IndexSearcher searcher = null;
    QueryParser queryParserBody = null;
    QueryParser queryParserTitle = null;

    public QueryHandler(String indexPathString) throws Exception{
        this.path = Paths.get(indexPathString);
        this.directory = FSDirectory.open(path);
        this.reader = DirectoryReader.open(directory);
        this.searcher = new IndexSearcher(reader);
        this.queryParserTitle = new QueryParser("title", AnalyzerFactory.giveTitleAnalyzer());
        this.queryParserBody = new QueryParser("body", AnalyzerFactory.giveBodyAnalyzer());
    }

    @SuppressWarnings("deprecation")
    public void executeQuery(String queryInput, int numberResultDocuments) throws Exception {
        Query query = null;
        if (queryInput.startsWith("title: ")) {
            String queryString = queryInput.replaceFirst("title: ", "");
            query = this.queryParserTitle.parse(queryString);
        } else if (queryInput.startsWith("body: ")){
            String queryString = queryInput.replaceFirst("body: ", "");
            query = this.queryParserBody.parse(queryString);
        } else {
            System.out.println("query input must be like <field>: <query>");
            return;
        }
        TopDocs hits = this.searcher.search(query, numberResultDocuments);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("doc"+scoreDoc.doc + ": "+ doc.get("title") + " (" + scoreDoc.score +")");
        }
    }

    public void closeDirectory() throws Exception{
        this.directory.close();
    }
}
