package Index;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class MainCsv {
    public static void main(String[] args) throws Exception {
        String indexPath = "/home/giordy/Documents/homework2/index";
        String documentsCsvPath = "/home/giordy/Documents/homework2/poki.csv";

        long startTime = System.currentTimeMillis();
        IndexHandler indexHandler = new IndexHandler(indexPath);
        List<ImmutablePair<String, String>> documents = DocumentsHandler.readDocumentsCSV(documentsCsvPath);
        indexHandler.generate(documents);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Index created in " + totalTime + " milliseconds");

        indexHandler.printIndexStats();
        indexHandler.closeIndexDirectory();

        DocumentsHandler.printDatasetStats(documents);
    }
}