package IndexGenerator;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
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

import com.opencsv.CSVReader;

public class Main {
    private static String readDocument(String documentPath) throws Exception {
        File documentFile = new File(documentPath);
        Scanner reader = new Scanner(documentFile);
        String content = "";
        while (reader.hasNextLine()) {
            content = content + reader.nextLine();
        }
        reader.close();
        return content;
    }

    @SuppressWarnings("unused")
    private static List<ImmutablePair<String, String>> readDocuments(String documentsFolderPath) throws Exception {
        File documentsFolder = new File(documentsFolderPath);
        List<ImmutablePair<String, String>> documents = new ArrayList<>();
        for (File documentFile : documentsFolder.listFiles()) {
            String fileName = documentFile.getName();
            String documentPath = documentsFolderPath + "/" + fileName;
            String documentName = fileName.substring(0, fileName.length()-4); //.txt
            String documentContent = readDocument(documentPath);
            ImmutablePair<String, String> document = new ImmutablePair<String,String>(documentName, documentContent);
            documents.add(document);
        }
        return documents;
    }

    private static List<ImmutablePair<String, String>> readDocumentsCSV(String csvPath) throws Exception {
        List<ImmutablePair<String, String>> documents = new ArrayList<>();
        CSVReader csvReader = new CSVReader(new FileReader(csvPath));
        String[] values = csvReader.readNext();
        while ((values = csvReader.readNext()) != null) {
            List<String> fields = Arrays.asList(values);
            ImmutablePair<String, String> document = new ImmutablePair<String,String>(fields.get(1), fields.get(4));
            documents.add(document);
        }
        return documents;
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        Path indexesPath = Paths.get("/home/giordy/Documents/homework2/index");
        Directory indexDirectory = FSDirectory.open(indexesPath);

        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
        Analyzer analyzerTitolo = CustomAnalyzer.builder()
            .withTokenizer(WhitespaceTokenizerFactory.class)
            .addTokenFilter(LowerCaseFilterFactory.class)
            .build();
        perFieldAnalyzers.put("title", analyzerTitolo);
        perFieldAnalyzers.put("body", new StandardAnalyzer());
        Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), perFieldAnalyzers);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //config.setCodec(new SimpleTextCodec());
        IndexWriter writer = new IndexWriter(indexDirectory, config);
        writer.deleteAll();

        String documentsCSVPath = "/home/giordy/Documents/homework2/poki.csv";
        List<ImmutablePair<String, String>> documents = readDocumentsCSV(documentsCSVPath);
        
        for (ImmutablePair<String, String> documentFields : documents) {
            String documentName = documentFields.left;
            String documentContent = documentFields.right;

            Document document = new Document();
            document.add(new TextField("title", documentName, Field.Store.YES));
            document.add(new TextField("body", documentContent, Field.Store.NO));

            writer.addDocument(document);
        }
        writer.commit();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        writer.close();

        System.out.println("Index created in " + totalTime + " milliseconds");
        IndexReader reader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Collection<String> indexedFields = FieldInfos.getIndexedFields(reader);
        for (String field : indexedFields) {
            System.out.println(searcher.collectionStatistics(field));
        }
        indexDirectory.close();
        printDatasetStats(documents);
    }

    private static void printDatasetStats(List<ImmutablePair<String, String>> documents) {
        float documentsNumber = 0;
        float totalCharsBody = 0;
        float totalCharsTitle = 0;
        for (ImmutablePair<String, String> documentFields : documents) {
            documentsNumber += 1;
            totalCharsBody += documentFields.right.length();
            totalCharsTitle += documentFields.left.length();
        }
        System.out.println("Total documents in csv: " + documentsNumber);
        System.out.println("Average chars per document title: " + (totalCharsTitle/documentsNumber));
        System.out.println("Average chars per document body: " + (totalCharsBody/documentsNumber));
    }
}