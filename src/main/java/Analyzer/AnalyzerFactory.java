package Analyzer;

import java.io.File;
import java.io.FileReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class AnalyzerFactory {
    public static Analyzer giveBodyAnalyzer () throws Exception{
        String stopwordsPath = "/home/giordy/Documents/homework2/stopwords.txt";
        File stopwordsFile = new File(stopwordsPath);
        FileReader reader = new FileReader(stopwordsFile);
        Analyzer bodyAnalyzer = new StandardAnalyzer(reader);
        return bodyAnalyzer;
    }

    public static Analyzer giveTitleAnalyzer () throws Exception{
        return new StandardAnalyzer();
    }
}