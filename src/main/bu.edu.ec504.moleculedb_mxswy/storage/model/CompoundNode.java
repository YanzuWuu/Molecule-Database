package bu.edu.ec504.moleculedb_mxswy.storage.model;

import bu.edu.ec504.moleculedb_mxswy.storage.model.Component;

import java.util.*;

public class CompoundNode {
    public Component component;

    // the lists of nodes connected to this node
    public List<CompoundNode> adjacencies = new ArrayList<>();

    // the lists of weights of the connections, or how many times that connection appears
    public List<Integer> adjacencyWeights = new ArrayList<>();

    public void addAdjacency(CompoundNode component, Integer weight){
        if (adjacencies.contains(component)){
            int componentIndex = adjacencies.indexOf(component);
            int currentWeight = adjacencyWeights.get(componentIndex);
            adjacencyWeights.set(componentIndex, currentWeight + 1);
        } else {
            adjacencies.add(component);
            adjacencyWeights.add(weight);
        }
    }
    /**
     * Define an equality measure for nodes. Two nodes are equal if they have the same Component, parents, and children.
     * @return boolean
     */
    public boolean isEqual(CompoundNode compareNode){
        // check if component is equal and sizes of parents and children are equal
        if (!compareNode.component.name.equals(component.name)){
            return false;
        } else if (compareNode.adjacencies.size() != adjacencies.size()) {
            return false;
        }

        // map adjacencies of this node to the other node
        return mapNodes(compareNode.adjacencies, adjacencies, compareNode.adjacencyWeights, adjacencyWeights);
    }

    /**
     * Function to map all nodes (either parents or children) to another node's. Returns "true" if a mapping is
     * successful and "false" if a mapping cannot be found.
     * @param targetList: list of CompoundNodes
     * @param queryList: list of CompoundNodes
     * @param targetWeights: corresponding list of weights
     * @param queryWeights: corresponding list of weights
     * @return boolean
     */
    private boolean mapNodes(List<CompoundNode> targetList, List<CompoundNode> queryList,
                             List<Integer> targetWeights, List<Integer> queryWeights){
        Boolean[] alreadyClaimed = new Boolean[queryList.size()];
        Arrays.fill(alreadyClaimed, Boolean.FALSE);

        for (int i=0; i< targetList.size(); i++){
            boolean matchFound = false;
            CompoundNode targetAdjacent = targetList.get(i);
            for (int j=0; j< queryList.size(); j++){
                CompoundNode queryAdjacent = queryList.get(j);

                if (!alreadyClaimed[j])
                    if (
                            targetAdjacent.component.name.equals(queryAdjacent.component.name) &&
                            targetWeights.get(i).equals(queryWeights.get(j)) &&
                            targetAdjacent.adjacencies.size() == queryAdjacent.adjacencies.size() &&
                            component.countAtoms(targetAdjacent.adjacencies).equals(component.countAtoms(queryAdjacent.adjacencies))
                    ) {
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

    /**
     * Define a constructor for CompoundNode which will initialize the node from a Component class
     */
    public CompoundNode(Component initializer){
        component=initializer;
    }
}
