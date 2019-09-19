package bu.edu.ec504.moleculedb_mxswy.storage.model;

import bu.edu.ec504.moleculedb_mxswy.api.CompoundService;
import bu.edu.ec504.moleculedb_mxswy.api.CompoundServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompoundTest {
    CompoundService myService = new CompoundServiceImpl();

    @BeforeEach
    void before() {
        // reset myService before each test
        myService = new CompoundServiceImpl();
    }

    @Test
    void testSameCompoundIsomorphism() throws URISyntaxException, IOException {
        Compound compoundI1 = loadCompound("/Inorganic/Water1.txt");
        Compound compoundI2 = loadCompound("/Inorganic/Water2.txt");

        assertTrue(compoundI1.isIsomorphic(compoundI2), "Water1 and Water2 should be isomorphic.");
    }

    @Test
    void testLargeCompoundIsomorphism() throws URISyntaxException, IOException {
        Compound compoundI1 = loadCompound("/Synthetic/GeneratedCompound: 931 atoms G5Io3ig6DuBetP0KO Permute 0.txt");
        Compound compoundI2 = loadCompound("/Synthetic/GeneratedCompound: 931 atoms G5Io3ig6DuBetP0KO Permute 0.txt");

        assertTrue(compoundI1.isIsomorphic(compoundI2), "Compound1 and Compound2 should be isomorphic.");
    }

    @Test
    void testSimilarCompoundIsomorphism() throws URISyntaxException, IOException {
        Compound compoundI1 = loadCompound("/Organic/1_2_Dimethyl_Benzene.txt");
        Compound compoundI2 = loadCompound("/Organic/1_3_Dimethyl_Benzene.txt");

        assertFalse(compoundI1.isIsomorphic(compoundI2), "1,2 Dimethyl Benzene should not be isomorphic to 1,3 Dimethyl Benzene.");
    }

    Compound loadCompound(String resourcePath) throws URISyntaxException, IOException {
        URL u1 = getClass().getResource(resourcePath);
        List<String> u1Lines = Files.readAllLines(Paths.get(u1.toURI()));
        return myService.loadFromFile(String.join("\n", u1Lines));
    }
}
