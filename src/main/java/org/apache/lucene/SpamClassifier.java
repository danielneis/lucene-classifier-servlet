package  org.apache.lucene;

import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;

import org.apache.lucene.analysis.standard.ClassicAnalyzer;

import org.apache.lucene.classification.SimpleNaiveBayesClassifier;
import org.apache.lucene.classification.ClassificationResult;

import org.apache.lucene.store.*;

import org.apache.lucene.util.Version;
import org.apache.lucene.util.BytesRef;

public final class SpamClassifier {
 
  public static final String indexDir = "/home/neis/solr-4.8.0/example/solr/collection2/data/index";
  public static final String[] CATEGORIES = { "spam", "ham" };
  private static int[][] confusionMatrix;
  private static Map<String, Integer> catindex;
 
  public static void main(String[] args) throws Exception {

    final long startTime = System.currentTimeMillis();

    SimpleNaiveBayesClassifier classifier = new SimpleNaiveBayesClassifier();

    DirectoryReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));  
    AtomicReader ar = SlowCompositeReaderWrapper.wrap(reader);
    classifier.train(ar, "text", "cat", new ClassicAnalyzer(Version.LUCENE_CURRENT));

    ClassificationResult<BytesRef> result = classifier.assignClass("Is this spam or not?");
    String classified = result.getAssignedClass().utf8ToString();
    System.out.println(classified);
    
    /*
    // This part tests classification with the same docs already stored

    confusionMatrix = new int[CATEGORIES.length][CATEGORIES.length];
    catindex = new HashMap<String, Integer>();
    for (int i = 0; i < CATEGORIES.length; i++) {
        catindex.put(CATEGORIES[i], i);
    }
 
    final int maxdoc = reader.maxDoc();
    for(int i = 0; i < maxdoc; i++){

      Document doc = ar.document(i);
      String correctAnswer = doc.get("cat");

      final int cai = catindex.get(correctAnswer);

      ClassificationResult<BytesRef> result = classifier.assignClass(doc.get("text"));
      String classified = result.getAssignedClass().utf8ToString();

      final int cli = catindex.get(classified);
      confusionMatrix[cai][cli]++;
    }

    final long endTime = System.currentTimeMillis();

    final int elapse = (int)(endTime - startTime) / 1000;
 
    // print results
    int fc = 0, tc = 0;
    for(int i = 0; i < CATEGORIES.length; i++){
      for(int j = 0; j < CATEGORIES.length; j++){
        System.out.printf(" %3d ", confusionMatrix[i][j]);
        if(i == j){
          tc += confusionMatrix[i][j];
        }
        else{
          fc += confusionMatrix[i][j];
        }
      }
      System.out.println();
    }
    float accrate = (float)tc / (float)(tc + fc);
    float errrate = (float)fc / (float)(tc + fc);
    System.out.printf("\n\n*** accuracy rate = %f, error rate = %f; time = %d (sec); %d docs\n", accrate, errrate, elapse, maxdoc);
    */
 
    reader.close();
  }
}
