package Index;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class MainTxt {
    public static void main(String[] args) throws Exception {
        String indexPath = "/home/giordy/Documents/homework2/index";
        String documentsPath = "/home/giordy/Documents/homework2/documents";

        long startTime = System.currentTimeMillis();
        IndexHandler indexHandler = new IndexHandler(indexPath);
        List<ImmutablePair<String, String>> documents = DocumentsHandler.readDocuments(documentsPath);
        indexHandler.generate(documents);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Index created in " + totalTime + " milliseconds");

        indexHandler.printIndexStats();
        indexHandler.closeIndexDirectory();

        DocumentsHandler.printDatasetStats(documents);
    }
}
