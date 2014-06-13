package  org.apache.lucene;

import java.io.BufferedReader;
import java.lang.StringBuffer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

public class SpamClassifier extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

        StringBuffer postText = new StringBuffer();

        String line = null;

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                postText.append(line);
        } catch (Exception e) { System.out.println(e.toString()); /*report an error*/ }

        final long startTime = System.currentTimeMillis();

        SimpleNaiveBayesClassifier classifier = new SimpleNaiveBayesClassifier();

        String indexDir1 = "/home/neis/solr-4.8.0/example/solr/collection2/data/index";

        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir1)));
        AtomicReader ar = SlowCompositeReaderWrapper.wrap(reader);
        classifier.train(ar, "text", "cat", new ClassicAnalyzer(Version.LUCENE_CURRENT));

        ClassificationResult<BytesRef> result = classifier.assignClass(postText.toString());
        String classified = result.getAssignedClass().utf8ToString();

        reader.close();

        resp.getWriter().print(classified);
    }
}
