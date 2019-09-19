package bu.edu.ec504.moleculedb_mxswy.generator;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

public class RandomCompoundGeneratorTest {
    @Test
    public void TestCompoundGenerator() throws IOException {
        RandomCompoundGenerator generator = new RandomCompoundGenerator();
        generator.generate(931, 5, 2);
    }

    @Test
    public void GenerateCompounds() throws IOException {
        RandomCompoundGenerator generator = new RandomCompoundGenerator();
        Random r = new Random();
        for (int i=0; i<8000; i++) {
            int numAtoms = r.nextInt(10000) + 1;
            generator.generate(numAtoms, 5, 1);
        }
    }
}
