package Index;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import Analyzer.AnalyzerFactory;

public class IndexHandler {
    private Analyzer analyzer = null;
    private IndexWriterConfig config = null;
    private Directory indexDirectory = null;
    private String indexPathString = null;

    public IndexHandler(String indexPathString) throws Exception{
        this.indexPathString = indexPathString;
        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
        Analyzer analyzerTitle = AnalyzerFactory.giveTitleAnalyzer();
        Analyzer analyzerBody = AnalyzerFactory.giveBodyAnalyzer();
        perFieldAnalyzers.put("title", analyzerTitle);
        perFieldAnalyzers.put("body", analyzerBody);
        this.analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), perFieldAnalyzers);
        this.config = new IndexWriterConfig(analyzer);
        //this.config.setCodec(new SimpleTextCodec());
    }

    public void generate(List<ImmutablePair<String, String>> documents) throws Exception {
        Path indexesPath = Paths.get(this.indexPathString);
        this.indexDirectory = FSDirectory.open(indexesPath);

        IndexWriter writer = new IndexWriter(indexDirectory, this.config);
        writer.deleteAll();
        
        for (ImmutablePair<String, String> documentFields : documents) {
            String documentName = documentFields.left;
            String documentContent = documentFields.right;

            Document document = new Document();
            document.add(new TextField("title", documentName, Field.Store.YES));
            document.add(new TextField("body", documentContent, Field.Store.YES));

            writer.addDocument(document);
        }
        writer.commit();
        writer.close();
    }

    public void printIndexStats() throws Exception {
        IndexReader reader = DirectoryReader.open(this.indexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Collection<String> indexedFields = FieldInfos.getIndexedFields(reader);
        for (String field : indexedFields) {
            System.out.println(searcher.collectionStatistics(field));
        }
    }

    public void closeIndexDirectory() throws Exception {
        this.indexDirectory.close();
    }
}