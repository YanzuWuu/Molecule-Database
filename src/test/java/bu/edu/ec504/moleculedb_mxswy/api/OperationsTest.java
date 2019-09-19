package bu.edu.ec504.moleculedb_mxswy.api;

import bu.edu.ec504.moleculedb_mxswy.search.SearchAPI;
import bu.edu.ec504.moleculedb_mxswy.search.SearchAPIImpl;
import bu.edu.ec504.moleculedb_mxswy.search.SearchTypes;
import bu.edu.ec504.moleculedb_mxswy.storage.api.StorageAPI;
import bu.edu.ec504.moleculedb_mxswy.storage.api.StorageAPIImpl;
import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OperationsTest {
    CompoundService myService = new CompoundServiceImpl();

    @BeforeEach
    void before() {
        // reset myService before each test
        myService = new CompoundServiceImpl();
    }

    @Test
    void testLoadFromFile() throws IOException {
        String path = new java.io.File(".").getCanonicalPath();
//        System.out.println(path);
        path= path + "/test/resources/Organic/benzene.txt";
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String st;
        while ((st = br.readLine()) != null)
            stringBuilder.append(st).append("\n");
        String finalString = stringBuilder.toString();
        br.close();
        Compound myCompound = myService.loadFromFile(finalString);
//        // check that Compound has name, root, and nodes
        assertEquals("benzene", myCompound.name);
        assertEquals("C", myCompound.root.component.name);
        assertEquals(12, myCompound.nodeList.size());
    }
    @Test
    void testSaveCompound() throws IOException {
        String fileData = "benzene\n" + "12\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "H\n" + "H\n" +
                "H\n" + "H\n" + "H\n" + "H\n" + "0 1\n" + "0 1\n" + "0 2\n" + "0 11\n" + "1 3\n" + "1 6\n" + "2 4\n" +
                "2 4\n" + "2 7\n" + "3 5\n" + "3 5\n" + "3 8\n" + "4 5\n" + "4 9\n" + "5 10\n";
        // Load Data as Compound class
        String current = new java.io.File(".").getCanonicalPath();
        Compound myCompound = myService.loadFromFile(fileData);
        myService.saveCompound(myCompound);
        String path = "/src/main/bu.edu.ec504.moleculedb_mxswy/storage/files/CompoundFiles/Compounds_12.txt";
        File compoundFile = new File(current+path);
        assertTrue(compoundFile.exists());
    }

    @Test
    void testDeleteCompound() throws IOException {
        String current = new java.io.File(".").getAbsolutePath();
        String path =current+ "/src/main/bu.edu.ec504.moleculedb_mxswy/storage/files/CompoundFiles/" +"Compounds_12" +".txt";
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder stringBuilder = new StringBuilder();
        String st;
        while ((st = br.readLine()) != null)
            stringBuilder.append(st).append("\n");
        String Previous = stringBuilder.toString();
        int ID =21; //Should Give a proper ID
        myService.deleteCompound(ID);
        BufferedReader br2 = new BufferedReader(new FileReader(path));
        StringBuilder stringBuilder2 = new StringBuilder();
        String st2;
        while ((st2 = br2.readLine()) != null)
            stringBuilder2.append(st2).append("\n");
        String finalString = stringBuilder2.toString();
        assertNotEquals(Previous,finalString);
    }

    @Test
    void testModifyCompound() throws IOException {
        String fileData = "benzene\n" + "12\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "H\n" + "H\n" +
                "H\n" + "H\n" + "H\n" + "H\n" + "0 1\n" + "0 1\n" + "0 2\n" + "0 11\n" + "1 3\n" + "1 6\n" + "2 4\n" +
                "2 4\n" + "2 7\n" + "3 5\n" + "3 5\n" + "3 8\n" + "4 5\n" + "4 9\n" + "5 10\n";
        Compound myCompound = myService.loadFromFile(fileData);

        String current = new java.io.File(".").getAbsolutePath();
        String path =current+ "/src/main/bu.edu.ec504.moleculedb_mxswy/storage/files/CompoundFiles/" +"Compounds_12" +".txt";
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder stringBuilder = new StringBuilder();
        String st;
        while ((st = br.readLine()) != null)
            stringBuilder.append(st).append("\n");
        String Previous = stringBuilder.toString();
        int ID =2; //Should Give a proper ID
        myService.modifyCompound(ID,myCompound);
        BufferedReader br2 = new BufferedReader(new FileReader(path));
        StringBuilder stringBuilder2 = new StringBuilder();
        String st2;
        while ((st2 = br2.readLine()) != null)
            stringBuilder2.append(st2).append("\n");
        String finalString = stringBuilder2.toString();
        assertNotEquals(Previous,finalString);
    }


    @Test
    void testSearchByName(){
        String fileData = "benzene\n" + "12\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "H\n" + "H\n" +
                "H\n" + "H\n" + "H\n" + "H\n" + "0 1\n" + "0 1\n" + "0 2\n" + "0 11\n" + "1 3\n" + "1 6\n" + "2 4\n" +
                "2 4\n" + "2 7\n" + "3 5\n" + "3 5\n" + "3 8\n" + "4 5\n" + "4 9\n" + "5 10\n";
        Compound newCompound = myService.loadFromFile(fileData);
        List<Integer> MatchID;
//        List<Integer> Expected = new ArrayList<Integer>();
        MatchID = myService.findCompound(newCompound, SearchTypes.NAME);
        System.out.print(MatchID);
//        assertEquals(Expected,MatchID);

    }

    @Test
    void testSearchByFormula(){
        String fileData = "benzene\n" + "12\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "H\n" + "H\n" +
                "H\n" + "H\n" + "H\n" + "H\n" + "0 1\n" + "0 1\n" + "0 2\n" + "0 11\n" + "1 3\n" + "1 6\n" + "2 4\n" +
                "2 4\n" + "2 7\n" + "3 5\n" + "3 5\n" + "3 8\n" + "4 5\n" + "4 9\n" + "5 10\n";
        Compound newCompound = myService.loadFromFile(fileData);
        List<Integer> MatchID;
        List<Integer> Expected = new ArrayList<Integer>();
        MatchID = myService.findCompound(newCompound, SearchTypes.CHEMICAL_FORMULA);
//        assertEquals(Expected,MatchID);
    }


    @Test
    void testSearchByIsomorphism(){
        String fileData = "benzene\n" + "12\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "H\n" + "H\n" +
                "H\n" + "H\n" + "H\n" + "H\n" + "0 1\n" + "0 1\n" + "0 2\n" + "0 11\n" + "1 3\n" + "1 6\n" + "2 4\n" +
                "2 4\n" + "2 7\n" + "3 5\n" + "3 5\n" + "3 8\n" + "4 5\n" + "4 9\n" + "5 10\n";
        Compound newCompound = myService.loadFromFile(fileData);
        List<Integer> MatchID;
        List<Integer> Expected = new ArrayList<Integer>();
        MatchID = myService.findCompound(newCompound, SearchTypes.ISOMORPHISM);
//        assertEquals(Expected,MatchID);
    }

    @Test
    void  testSearchBySubgraph(){
        String fileData = "benzene\n" + "12\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "H\n" + "H\n" +
                "H\n" + "H\n" + "H\n" + "H\n" + "0 1\n" + "0 1\n" + "0 2\n" + "0 11\n" + "1 3\n" + "1 6\n" + "2 4\n" +
                "2 4\n" + "2 7\n" + "3 5\n" + "3 5\n" + "3 8\n" + "4 5\n" + "4 9\n" + "5 10\n";
        Compound newCompound = myService.loadFromFile(fileData);
        List<Integer> MatchID;
        List<Integer> MatchID2;
        MatchID = myService.findCompound(newCompound, SearchTypes.EXACT_SUBGRAPH);
        MatchID = myService.findCompound(newCompound, SearchTypes.FUZZY_SUBGRAPH);

    }



    //Test the 10 operations in 1 second
    @Test()
    void speedTest(){
        String fileData = "benzene\n" + "12\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "H\n" + "H\n" +
                "H\n" + "H\n" + "H\n" + "H\n" + "0 1\n" + "0 1\n" + "0 2\n" + "0 11\n" + "1 3\n" + "1 6\n" + "2 4\n" +
                "2 4\n" + "2 7\n" + "3 5\n" + "3 5\n" + "3 8\n" + "4 5\n" + "4 9\n" + "5 10\n";
        // Load Data as Compound class
        long startTime=System.currentTimeMillis();
        for(int i=0; i<10; i++){
            Compound myCompound = myService.loadFromFile(fileData);
            myService.saveCompound(myCompound);

        }
        long endTime=System.currentTimeMillis();
        double operationTime = endTime - startTime;
        System.out.print("The operation time: "+operationTime+"ms");  //Unit as millis seconds
        Assertions.assertTimeout(Duration.ofSeconds(1),()->{
            for(int i=0; i<10; i++){
                Compound myCompound = myService.loadFromFile(fileData);
                myService.saveCompound(myCompound);

            }

        });

    }



}