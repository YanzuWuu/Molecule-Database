package bu.edu.ec504.moleculedb_mxswy.search;

import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;
import bu.edu.ec504.moleculedb_mxswy.storage.api.StorageAPI;
import bu.edu.ec504.moleculedb_mxswy.storage.model.CompoundNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchAPIImpl implements SearchAPI {
    private StorageAPI storageAPI;

    /**
     * Function for finding a compound by name given a Compound object
     *
     * @param compoundData : Compound object loaded from file content
     * @return String compoundID or "Not Found"
     */
    @Override
    public List<Integer> byName(Compound compoundData) {
        // narrow down search options to matching chemical formula
        List<Integer> searchCandidates = byChemicalFormula(compoundData);

        // create new arraylist for matches
        List<Integer> matchingIDs = new ArrayList<>();
        for (int candidateID: searchCandidates) {
            Compound candidateCompound = storageAPI.getCompound(candidateID);
            if (candidateCompound.name.equals(compoundData.name)){
                matchingIDs.add(candidateID);
            }
        }
        return matchingIDs;
    }

    /**
     * Function for finding a compound by chemical formula given a Compound object
     *
     * @param compoundData : Compound object loaded from file content
     * @return Arraay of String compoundID or "Not Found"
     */
    @Override
    public List<Integer> byChemicalFormula(Compound compoundData) {
        // compute num nodes and num edges
        int numNodes = compoundData.nodeList.size();
        int numEdges = compoundData.countEdges();

        Map<String, Integer> atomCount = compoundData.countAtoms(compoundData.nodeList);

        List<Integer> searchCandidates = storageAPI.getIDsForSameSizeCompound(numNodes, numEdges);
        List<Integer> matchingIDs = new ArrayList<>();
        for (int candidateID: searchCandidates) {
            Compound candidateCompound = storageAPI.getCompound(candidateID);
            if (candidateCompound.countAtoms(candidateCompound.nodeList).equals(atomCount)){
                matchingIDs.add(candidateID);
            }
        }

        return matchingIDs;
    }

    /**
     * Function for finding a compound by isomorphism given a Compound object
     *
     * @param compoundData : Compound object loaded from file content
     * @return String compoundID or "Not Found"
     */
    @Override
    public List<Integer> byIsomorphism(Compound compoundData) {
        // compute num nodes and num edges
        int numNodes = compoundData.nodeList.size();
        int numEdges = compoundData.countEdges();

        List<Integer> searchCandidates = storageAPI.getIDsForSameSizeCompound(numNodes, numEdges);
        List<Integer> matchingIDs = new ArrayList<>();
        for (int candidateID: searchCandidates) {
            Compound candidateCompound = storageAPI.getCompound(candidateID);
            if (candidateCompound.isIsomorphic(compoundData)){
                matchingIDs.add(candidateID);
            }
        }

        return matchingIDs;
    }

    /**
     * Function for finding a compound up to subgraph isomorphism given a Compound object
     *
     * @param compoundData : Compound object loaded from file content
     * @param searchMode   : "exact" or "fuzzy" for types of subgraph search
     * @return List containing the compoundIDs
     */
    @Override
    public List<Integer> bySubgraph(Compound compoundData, String searchMode) {
        /*FUZZY_GRAPH_DIFF is a rating scale for the maximum number of differences can exist between the given subgraph
         *and any subgraph in the graph database. Each node that has a different symbol is +1 difference and each edge
         *that has a different weight has is +1 difference.
         */
        int FUZZY_GRAPH_DIFF = 2;
        ArrayList<Integer> potentialMatches = storageAPI.getIDsForSameOrLargerSizeCompound(compoundData.nodeList.size());

        List<Integer> matchingIDs = new ArrayList<>();
        if(searchMode.equals("Fuzzy")){
            for(int i = 0; i < potentialMatches.size(); i++){
                if(subgraphSearch(compoundData, storageAPI.getCompound(potentialMatches.get(i)), FUZZY_GRAPH_DIFF)){
                    matchingIDs.add(potentialMatches.get(i));
                }
            }
        }
        else{
            for(int i = 0; i < potentialMatches.size(); i++){
                if(subgraphSearch(compoundData, storageAPI.getCompound(potentialMatches.get(i)), 0)){
                    matchingIDs.add(potentialMatches.get(i));
                }
            }
        }
        return matchingIDs;
    }

    /**
     * Function that returns true if subgraph is present in graph with numDiffs number of differences
     * @param subgraph Compound object containing the subgraph
     * @param graph Compound object containing the graph
     * @param numDiffs number of differences to search for the subgraph with
     * @return boolean of whether the subgragh was found or not
     */
    private boolean subgraphSearch(Compound subgraph, Compound graph, int numDiffs){
        boolean result = false;
        ArrayList<Integer> nodeMap = new ArrayList<>();
        if(search(subgraph, graph, numDiffs, nodeMap)){
            result = true;
        }
        return result;
    }

    /**
     * Function that returns whether a subgraph is present in graph with given number of differences
     * @param subgraph subgraph being searched for
     * @param graph graph which the subgraph is being searched in
     * @param numDiffs the number of allowable differences for a successful subgraph search
     * @param nodeMap current mapping of subgraph nodes to nodes in the graph
     * @return true if subgraph is present in the graph
     */
    private boolean search(Compound subgraph, Compound graph, int numDiffs, ArrayList<Integer> nodeMap){
        int mappedNodes = nodeMap.size();
        float numErrors = 0;

        //Go through every edge of the nodes from subgraph that are already mapped
        for(int i = 0; i < mappedNodes; i++) {
            CompoundNode node = subgraph.nodeList.get(i);
            for (int j = 0; j < node.adjacencies.size(); j++) {
                CompoundNode adjNode = node.adjacencies.get(j);
                int adjIndex = subgraph.nodeList.indexOf(adjNode);
                if (adjIndex < mappedNodes) {
                    CompoundNode mappedNode = graph.nodeList.get(nodeMap.get(i));
                    CompoundNode mappedAdjNode = graph.nodeList.get(nodeMap.get(adjIndex));
                    //Fail permutation if there are no edges between mappedNode and mappedAdjNode
                    if (!mappedNode.adjacencies.contains(mappedAdjNode)) {
                        return false;
                    } else {
                        //Check if the node value are the same on mapped nodes from subgraph to graph
                        String nodeName = node.component.name.trim();
                        if (!nodeName.equals(mappedNode.component.name.trim())) {
                            numErrors += (float)1/node.adjacencies.size();
                        }
                        int mappedIndex = mappedNode.adjacencies.indexOf(mappedAdjNode);
                        //Check if the edge weight of subgraph and graph are different between the two nodes
                        if (mappedNode.adjacencyWeights.get(mappedIndex) != node.adjacencyWeights.get(j)) {
                            numErrors += .5;
                        }
                    }
                }
            }
        }
        //Fail permutation when the number of differences is greater than the number of allowable differences
        if(numErrors > numDiffs){
            return false;
        }
        //Successful mapping when all nodes in subgraph are mapped to nodes in graph
        if(mappedNodes == subgraph.nodeList.size()){
            return true;
        }
        //Run through every permutation of subgraph node to graph node mapping
        for(int i = 0; i < graph.nodeList.size(); i++) {
            if(!nodeMap.contains(i)){
                nodeMap.add(i);
                if(search(subgraph, graph, numDiffs, nodeMap)){
                    return true;
                }
                nodeMap.remove(nodeMap.size() - 1);
            }
        }
        return false;
    }

    public void setStorageAPI(StorageAPI storageAPI) {
        this.storageAPI = storageAPI;
    }
}
