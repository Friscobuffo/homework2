package Index;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.opencsv.CSVReader;

public class DocumentsHandler {
    public static List<ImmutablePair<String, String>> readDocumentsCSV(String csvPath) throws Exception {
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
    public static List<ImmutablePair<String, String>> readDocuments(String documentsFolderPath) throws Exception {
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

    public static void printDatasetStats(List<ImmutablePair<String, String>> documents) {
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