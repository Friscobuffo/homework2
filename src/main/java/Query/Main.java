package Query;

import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) throws Exception {
        String indexPath = "/home/giordy/IdeaProjects/homework2/index";
        QueryHandler queryHandler = new QueryHandler(indexPath);

        Scanner scanner = new Scanner(System.in);
        String anotherQuery;
        do {
            System.out.println("Enter query:");
            String queryString = scanner.nextLine();
            queryHandler.executeQuery(queryString, 5);
            System.out.println("Make another query? (y/n)");
            anotherQuery = scanner.nextLine();
        } while (anotherQuery.equals("y"));
        System.out.println("Bye bye");
        queryHandler.closeDirectory();
        scanner.close();
    }
}