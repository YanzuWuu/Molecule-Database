package bu.edu.ec504.moleculedb_mxswy.api;

import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompoundServiceTest {
    CompoundService myService = new CompoundServiceImpl();

    @BeforeEach
    void before() {
    // reset myService before each test
    myService = new CompoundServiceImpl();
    }

    @Test
    void testLoadFromFile() {
        String fileData = "molecule_name\n" +
                "5\n" +
                "C\n" +
                "H\n" +
                "H\n" +
                "H\n" +
                "H\n" +
                "0 1\n" +
                "0 2\n" +
                "0 3\n" +
                "0 4\n";

        Compound myCompound = myService.loadFromFile(fileData);

        // check that Compound has name, root, and nodes
        assertEquals("molecule_name", myCompound.name);
        assertEquals("C", myCompound.root.component.name);
        assertEquals(5, myCompound.nodeList.size());
    }


    @Test
    void testSaveCompound() throws IOException {
        String fileData = "benzene\n" + "12\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "C\n" + "H\n" + "H\n" +
                "H\n" + "H\n" + "H\n" + "H\n" + "0 1\n" + "0 1\n" + "0 2\n" + "0 11\n" + "1 3\n" + "1 6\n" + "2 4\n" +
                "2 4\n" + "2 7\n" + "3 5\n" + "3 5\n" + "3 8\n" + "4 5\n" + "4 9\n" + "5 10\n";
        // Load Data as Compound class
        String current1 = new java.io.File(".").getCanonicalPath();
        Paths.get(current1).getParent();
        System.out.print("Current: "+current1);
        System.out.print("\nParent "+Paths.get(current1).getParent()+"\n");
        Compound myCompound = myService.loadFromFile(fileData);
        myService.saveCompound(myCompound);
//        String path = "../main/bu.edu.ec504.moleculedb_mxswy/storage/files/CompoundFiles/" +"Compounds_12" +".txt";
//        File compoundFile = new File(path);
//        assertTrue(compoundFile.exists());
    }
}
