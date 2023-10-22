package IndexGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class Main {
    private static String readDocument(String documentPath) throws FileNotFoundException {
        File documentFile = new File(documentPath);
        Scanner reader = new Scanner(documentFile);
        String content = "";
        while (reader.hasNextLine()) {
            content = content + reader.nextLine();
        }
        reader.close();
        return content;
    }

    private static List<ImmutablePair<String, String>> readDocuments(String documentsFolderPath) throws FileNotFoundException {
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

    private static List<ImmutablePair<String, String>> readDocumentsCSV(String csvPath) throws FileNotFoundException, IOException, CsvValidationException {
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

    public static void main(String[] args) throws IOException, CsvValidationException {
        Path indexesPath = Paths.get("/home/giordy/Documents/homework2/index");
        Directory indexDirectory = FSDirectory.open(indexesPath);
        Analyzer defaultAnalyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(defaultAnalyzer);
        IndexWriter writer = new IndexWriter(indexDirectory, config);
        writer.deleteAll();

        String documentsCSVPath = "/home/giordy/Documents/homework2/poki.csv";
        List<ImmutablePair<String, String>> documents = readDocumentsCSV(documentsCSVPath);
        
        for (ImmutablePair<String, String> documentFields : documents) {
            String documentName = documentFields.left;
            String documentContent = documentFields.right;

            Document document = new Document();
            document.add(new TextField("titolo", documentName, Field.Store.YES));
            document.add(new TextField("contenuto", documentContent, Field.Store.YES));

            writer.addDocument(document);
        }
        writer.commit();
        writer.close();
        indexDirectory.close();
    }
}