package bu.edu.ec504.moleculedb_mxswy.storage.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CompoundNodeTest {
    @Test
    void testIsEqual(){
        // construct two equivalent nodes
        CompoundNode n1 = new CompoundNode(new Atom("X"));
        CompoundNode n2 = new CompoundNode(new Atom("X"));

        // construct some helper nodes to create adjacencies with
        CompoundNode h1 = new CompoundNode(new Atom("Y"));
        CompoundNode h2 = new CompoundNode(new Atom("Z"));
        n1.addAdjacency(h1, 1);
        n1.addAdjacency(h2, 2);

        CompoundNode h3 = new CompoundNode(new Atom("Y"));
        CompoundNode h4 = new CompoundNode(new Atom("Z"));
        n2.addAdjacency(h3, 1);
        n2.addAdjacency(h4, 2);

        assertTrue(n1.isEqual(n2));

    }
}
