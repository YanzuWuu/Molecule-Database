package bu.edu.ec504.moleculedb_mxswy.generator;

import javafx.util.Pair;
import bu.edu.ec504.moleculedb_mxswy.storage.model.Atom;
import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;
import bu.edu.ec504.moleculedb_mxswy.storage.model.CompoundNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneratedCompound extends Compound {
    List<Integer> orphanNodeIndices = new ArrayList<>();
    List<Pair<CompoundNode, CompoundNode>> edges = new ArrayList<>();
    public GeneratedCompound(String name) {
        super(name);
    }

    public boolean hasOrphanAtoms() {
        return orphanNodeIndices.size() > 0;
    }

    public void addAtom(String s) {
        Atom myAtom = new Atom(s);
        CompoundNode myNode = new CompoundNode(myAtom);
        nodeList.add(myNode);
        orphanNodeIndices.add(nodeList.indexOf(myNode));
    }

    public void addEdge(Pair<CompoundNode, CompoundNode> edge){
        edge.getKey().addAdjacency(edge.getValue(), 1);
        edge.getValue().addAdjacency(edge.getKey(), 1);

        int edgeIndex1 = nodeList.indexOf(edge.getKey());
        int edgeIndex2 = nodeList.indexOf(edge.getValue());

        if (orphanNodeIndices.contains(edgeIndex1)){
            orphanNodeIndices.remove(orphanNodeIndices.indexOf(edgeIndex1));
        }

        if (orphanNodeIndices.contains(edgeIndex2)){
            orphanNodeIndices.remove(orphanNodeIndices.indexOf(edgeIndex2));
        }
    }

    public int countEdges(CompoundNode node) {
        return node.adjacencyWeights.stream().reduce(0, (integer, integer2) -> integer + integer2);
    }

    public void shuffle(){
        Collections.shuffle(nodeList);
    }

    public void writeFile(String filename) throws IOException {
        StringBuilder output = new StringBuilder();

        // write name
        output.append(this.name).append("\n");

        // write number of atoms
        output.append(nodeList.size()).append("\n");

        // write all atoms
        for (CompoundNode compoundNode : nodeList) {
            output.append(compoundNode.component.name).append("\n");
        }

        // write all edges
        for (int i=0; i<nodeList.size(); i++){
            CompoundNode node = nodeList.get(i);
            for (int j=0; j<node.adjacencies.size(); j++){
                CompoundNode adjacentNode = node.adjacencies.get(j);
                for (int k=0; k < node.adjacencyWeights.get(j); k++){
                    output.append(i).append(" ").append(nodeList.indexOf(adjacentNode)).append("\n");
                }
            }
        }

        // write to file
        Files.write(Paths.get(filename), output.toString().getBytes(StandardCharsets.UTF_8));
    }
}
