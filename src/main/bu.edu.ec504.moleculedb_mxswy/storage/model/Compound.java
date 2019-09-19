package bu.edu.ec504.moleculedb_mxswy.storage.model;

import java.util.*;

/**
 *  Define the Compound class as an interconnected node structure containing adjacency nodes. By convention, the root of the graph
 *  should be the alphabetically lowest node in the graph based on element symbol.
 */
public class Compound extends Component {
    public CompoundNode root;
    public List<CompoundNode> nodeList = new ArrayList<>();

    /**
     * Function to count the number of edges in this compound
     * @return int number of edges in this compound
     */
    public int countEdges(){
        int numNodes = nodeList.size();
        int numEdges = 0;
        for(CompoundNode node: nodeList) {
            for(int weight: node.adjacencyWeights) {
                numEdges += weight;
            }
        }
        // divide the number of edges by 2 since each edge got double counted
        return numEdges/2;
    }



    public boolean isIsomorphic(Compound otherCompound){
        // This is a graph isomorphism problem, which NP-complete. However, we can check for non-isomorphisms easily.
        if (
                (otherCompound.nodeList.size() != nodeList.size())
                || (this.countEdges() != otherCompound.countEdges())
                || !(this.countAtoms(nodeList).equals(otherCompound.countAtoms(otherCompound.nodeList)))
        ) return false;

        // Now iterate over all the nodes in the nodeList and map them to the other compound
        Boolean[] alreadyClaimed = new Boolean[nodeList.size()];
        Arrays.fill(alreadyClaimed, Boolean.FALSE);

        for (int i=0; i<nodeList.size(); i++){
            boolean matchFound = false;
            CompoundNode targetNode = nodeList.get(i);
            for (int j=0; j< otherCompound.nodeList.size(); j++){
                CompoundNode queryNode = otherCompound.nodeList.get(j);
                if (targetNode.isEqual(queryNode) && !alreadyClaimed[j]){
                    matchFound = true;
                    alreadyClaimed[j] = true;
                    break;
                }
            }

            if (!matchFound){
                return false;
            }
        }
        return true;
    }
    public Compound(String name){
        super(name);
    }
}

