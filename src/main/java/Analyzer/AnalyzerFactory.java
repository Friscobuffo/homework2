package Analyzer;

import java.io.File;
import java.io.FileReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class AnalyzerFactory {
    public static Analyzer giveBodyAnalyzer () throws Exception{
        String stopwordsPath = "/home/giordy/IdeaProjects/homework2/stopwords.txt";
        File stopwordsFile = new File(stopwordsPath);
        FileReader reader = new FileReader(stopwordsFile);
        return new StandardAnalyzer(reader);
    }

    public static Analyzer giveTitleAnalyzer () {
        return new StandardAnalyzer();
    }
}