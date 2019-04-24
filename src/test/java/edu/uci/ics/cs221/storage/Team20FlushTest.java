package edu.uci.ics.cs221.storage;

import edu.uci.ics.cs221.analysis.NaiveAnalyzer;
import edu.uci.ics.cs221.index.inverted.InvertedIndexManager;
import edu.uci.ics.cs221.index.inverted.InvertedIndexSegmentForTest;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;


import static org.junit.Assert.assertEquals;

public class Team20FlushTest {

    /* Change the flush threshold and see if flush() is automatically called when the threshold is reached.
    * In this test, we set the threshold to 2. We then add two documents, after which flush() should be
    * automatically called*/
    @Test
    public void check_auto_flush_call() {
        Document d1 = new Document("cat dog");
        Document d2 = new Document("cat elephant");
        Map<String, List<Integer>> expectedPostingList = new HashMap<>();
        expectedPostingList.put("cat", Arrays.asList(0, 1));
        expectedPostingList.put("dog", Arrays.asList(0)) ;
        expectedPostingList.put("elephant", Arrays.asList(1)) ;
        Map<Integer, Document> expectedDocStore = new HashMap<>();
        expectedDocStore.put(0, d1);
        expectedDocStore.put(1, d2);
        NaiveAnalyzer analyzer = new NaiveAnalyzer();
        InvertedIndexManager ii = InvertedIndexManager.createOrOpen("index/Team20FlushTest/", analyzer) ;

        ii.DEFAULT_FLUSH_THRESHOLD = 2;
        ii.addDocument(d1);
        ii.addDocument(d2);

        assertEquals(1, ii.getNumSegments());
        InvertedIndexSegmentForTest segment;
        segment = ii.getIndexSegment(0);
        assertEquals(expectedPostingList, segment.getInvertedLists());
        assertEquals(expectedDocStore, segment.getDocuments());
    }

    /* Change the flush threshold and see if flush() is automatically called twice when the threshold is reached.
     * In this test, we set the threshold to 2. We then add four documents. flush() should be
     * automatically called twice*/
    @Test
    public void check_auto_flush_call_twice() {
        Document d1 = new Document("cat dog");
        Document d2 = new Document("cat elephant");
        Document d3 = new Document("call car cast");
        Document d4 = new Document("call canon cast");

        Map<String, List<Integer>> expectedPostingList1 = new HashMap<>();
        expectedPostingList1.put("cat", Arrays.asList(0, 1));
        expectedPostingList1.put("dog", Arrays.asList(0)) ;
        expectedPostingList1.put("elephant", Arrays.asList(1)) ;
        Map<Integer, Document> expectedDocStore1 = new HashMap<>();
        expectedDocStore1.put(0, d1);
        expectedDocStore1.put(1, d2);

        Map<String, List<Integer>> expectedPostingList2 = new HashMap<>();
        expectedPostingList2.put("call", Arrays.asList(0, 1));
        expectedPostingList2.put("canon", Arrays.asList(1)) ;
        expectedPostingList2.put("car", Arrays.asList(0)) ;
        expectedPostingList2.put("cast", Arrays.asList(0, 1)) ;
        Map<Integer, Document> expectedDocStore2 = new HashMap<>();
        expectedDocStore2.put(0, d3);
        expectedDocStore2.put(1, d4);

        NaiveAnalyzer analyzer = new NaiveAnalyzer();
        InvertedIndexManager ii = InvertedIndexManager.createOrOpen("index/Team20FlushTest/", analyzer) ;

        ii.DEFAULT_FLUSH_THRESHOLD = 2;
        ii.addDocument(d1);
        ii.addDocument(d2);
        ii.addDocument(d3);
        ii.addDocument(d4);

        assertEquals(2, ii.getNumSegments());

        InvertedIndexSegmentForTest segment0;
        segment0 = ii.getIndexSegment(0);
        assertEquals(expectedPostingList1, segment0.getInvertedLists());
        assertEquals(expectedDocStore1, segment0.getDocuments());

        InvertedIndexSegmentForTest segment1;
        segment1 = ii.getIndexSegment(1);
        assertEquals(expectedPostingList2, segment1.getInvertedLists());
        assertEquals(expectedDocStore2, segment1.getDocuments());
    }

    /* Forcefully call flush(), keeping an empty in-memory buffer, flush() should do nothing*/
    @Test
    public void check_empty_mem_buffer() {
        NaiveAnalyzer analyzer = new NaiveAnalyzer();
        InvertedIndexManager ii = InvertedIndexManager.createOrOpen("index/Team20FlushTest/", analyzer) ;

        ii.flush();
        assertEquals(0, ii.getNumSegments());
    }

    @Test
    public void test3(){

        /* check if two segments have been created after two calls to flush */

        Document d1 = new Document("cat dog");
        Document d2 = new Document("cat elephant");
        Map<String, List<Integer>> expectedPostingList1 = new HashMap<>();
        Map<String, List<Integer>> expectedPostingList2 = new HashMap<>();
        expectedPostingList1.put("cat", Arrays.asList(0));
        expectedPostingList1.put("dog", Arrays.asList(0)) ;
        expectedPostingList2.put("cat", Arrays.asList(0));
        expectedPostingList2.put("elephant", Arrays.asList(0)) ;
        Map<Integer, Document> expectedDocuments1 = new HashMap<>();
        Map<Integer, Document> expectedDocuments2 = new HashMap<>();
        expectedDocuments1.put(0, d1);
        expectedDocuments2.put(0, d2);
        NaiveAnalyzer analyzer = new NaiveAnalyzer();
        InvertedIndexManager ii = InvertedIndexManager.createOrOpen("index/Team20FlushTest/", analyzer) ;
        ii.DEFAULT_FLUSH_THRESHOLD = 5;
        ii.addDocument(d1);
        ii.flush();
        ii.addDocument(d2);
        ii.flush();
        assertEquals(2, ii.getNumSegments());
        InvertedIndexSegmentForTest segment0;
        InvertedIndexSegmentForTest segment1;
        segment0 = ii.getIndexSegment(0);
        assertEquals(expectedPostingList1, segment0.getInvertedLists());
        assertEquals(expectedDocuments1, segment0.getDocuments());
        segment1 = ii.getIndexSegment(1);
        assertEquals(expectedPostingList2, segment1.getInvertedLists());
        assertEquals(expectedDocuments2, segment1.getDocuments());
    }



    @AfterClass
    public static void deleteFiles() {
               String SRC_FOLDER = "index/Team20FlushTest/";

                File directory = new File(SRC_FOLDER);

                //make sure directory exists
                if(!directory.exists()){

                    System.out.println("Directory does not exist.");
                    System.exit(0);

                }else{

                    try{

                        delete(directory);

                    }catch(IOException e){
                        e.printStackTrace();
                        System.exit(0);
                    }
                }

                System.out.println("Done");
            }

            public static void delete(File file)
                    throws IOException {

                if(file.isDirectory()){

                    //directory is empty, then delete it
                    if(file.list().length==0){

                        file.delete();
                        System.out.println("Directory is deleted : "
                                + file.getAbsolutePath());

                    }else{

                        //list all the directory contents
                        String files[] = file.list();

                        for (String temp : files) {
                            //construct the file structure
                            File fileDelete = new File(file, temp);

                            //recursive delete
                            delete(fileDelete);
                        }

                        //check the directory again, if empty then delete it
                        if(file.list().length==0){
                            file.delete();
                            System.out.println("Directory is deleted : "
                                    + file.getAbsolutePath());
                        }
                    }

                }else{
                    //if file, then delete it
                    file.delete();
                    System.out.println("File is deleted : " + file.getAbsolutePath());
                }
            }
        }



