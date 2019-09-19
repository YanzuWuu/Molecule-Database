package bu.edu.ec504.moleculedb_mxswy.api;

import bu.edu.ec504.moleculedb_mxswy.Pubchem.Pubchem;
import bu.edu.ec504.moleculedb_mxswy.exceptions.InvalidFormatException;
import bu.edu.ec504.moleculedb_mxswy.search.SearchAPI;
import bu.edu.ec504.moleculedb_mxswy.search.SearchAPIImpl;
import bu.edu.ec504.moleculedb_mxswy.search.SearchTypes;
import bu.edu.ec504.moleculedb_mxswy.storage.api.StorageAPI;
import bu.edu.ec504.moleculedb_mxswy.storage.api.StorageAPIImpl;
import bu.edu.ec504.moleculedb_mxswy.storage.model.Component;
import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;
import bu.edu.ec504.moleculedb_mxswy.storage.model.CompoundNode;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CompoundServiceImpl implements CompoundService {
    public void setStorageAPI(StorageAPI storageAPI) {
        this.storageAPI = storageAPI;
    }

    public void setSearchAPI(SearchAPI searchAPI) {
        this.searchAPI = searchAPI;
    }

    public void setPubchemService(Pubchem pubchemService) { this.pubchemService = pubchemService; }

    private StorageAPI storageAPI;
    private SearchAPI searchAPI;
    private Pubchem pubchemService;

    public CompoundServiceImpl() {
        this.storageAPI = new StorageAPIImpl();
        this.searchAPI = new SearchAPIImpl();
        this.pubchemService = new Pubchem();
        ((SearchAPIImpl) this.searchAPI).setStorageAPI(this.storageAPI);
    }

    /**
     * Function for finding a compound up to isomorphism given a Compound object
     * @param compoundData : Compound object loaded from file content
     * @param searchType : Field to search by, must be one of "Name", "Formula", "Isomorphism", "Exact Subgraph" or "Fuzzy Subgraph"
     * @return an int compountID or -1 for "Not Found"
     */
    public List<Integer> findCompound(Compound compoundData, SearchTypes searchType){
        if (searchType == null){
            throw new IllegalArgumentException("Search type not specified.");
        }
        switch (searchType){
            case NAME:
                return searchAPI.byName(compoundData);
            case CHEMICAL_FORMULA:
                return searchAPI.byChemicalFormula(compoundData);
            case ISOMORPHISM:
                return searchAPI.byIsomorphism(compoundData);
            case FUZZY_SUBGRAPH:
                return searchAPI.bySubgraph(compoundData, "Fuzzy");
            case EXACT_SUBGRAPH:
                return searchAPI.bySubgraph(compoundData, "Exact");
        }
        // this shouldn't ever happen
        return null;
    }

    /**
     * Function for saving a new compound in the database from a Compound object
     * @param compoundData: Compound object loaded from file content
     */
    public int saveCompound(Compound compoundData) {
        return storageAPI.saveCompound(compoundData);
    }

    /**
     * Function for deleting a compound from the database using the compound name or unique identifier
     * @param compoundID: String containing unique compound identifier
     */
    public void deleteCompound(int compoundID){
        storageAPI.deleteCompound(compoundID);
    }

    /**
     * Function for updating or overwriting an existing compound with new data using the compound name and the new data.
     * @param compoundID: String containing unique compound identifier
     * @param newData: Compound object loaded from file content to overwrite existing stored Compound.
     */
    public void modifyCompound(int compoundID, Compound newData){
    	storageAPI.deleteCompound(compoundID);
    	storageAPI.saveCompound(newData);
    }

    /**
     * Function to load a specific ID from PubChem into the database
     * @param ID: PubChem ID of target molecule
     */
    public void pubchemLoadID(int ID) throws IOException {
        String[] LII = new String[]{"-i", Integer.toString(ID)};
        pubchemService.PubChemAPI(LII);
    }

    /**
     * Function to load a random number of compounds from PubChem into the database
     * @param numMolecules: Number of molecules to load into the database.
     */
    public void pubchemLoadRandom(int numMolecules) throws IOException {
        String[] LRR = new String[]{"-r", Integer.toString(numMolecules)};
        pubchemService.PubChemAPI(LRR);
    }

    /**
     * Helper function to convert text file contents to a Compound object
     * @param fileContent: The contents of a text file describe a molecule adjacency matrix
     * @return Compound object
     */
    public Compound loadFromFile(String fileContent){
        String[] fileLines = fileContent.split("\n");
        // define the compound to return and store first line of file as name
        if (fileLines.length < 2){
            throw new InvalidFormatException("Compound data is empty or malformed.");
        }
        Compound loadedCompound = new Compound(fileLines[0].trim());

        // the second line of the file contains the number of vertices
        int num_vertices;
        try {
            num_vertices = Integer.valueOf(fileLines[1].trim());
        } catch (NumberFormatException e){
            throw new InvalidFormatException("Second line of compound data must be integer containing number of vertices.");
        }

        // read file contents into a list of Components and a list of adjacencies
        List<Component> componentList = new ArrayList<>();
        for (int i=2; i<num_vertices + 2; i++) {
            Component node = new Component(fileLines[i].trim());
            componentList.add(node);
            //Atom myAtom = new Atom(fileLines[i]);
            //componentList.add(myAtom);
        }

        List<Pair<Integer, Integer>> adjacencyList = new ArrayList<>();
        for (int i=2+num_vertices; i<fileLines.length; i++){
                // check that first character is an int

                String[] tuple = fileLines[i].split("\\s+");
                int edge_parent;
                int edge_child;
                try{
                    edge_parent=Integer.parseInt(tuple[0]);
                    edge_child=Integer.parseInt(tuple[1]);
                } catch (NumberFormatException e){
                    throw new InvalidFormatException("Edges must be specified as integers.");
                } catch (ArrayIndexOutOfBoundsException e){
                    throw new InvalidFormatException("Second node of edge not found on line " + i);
                }

                Pair<Integer, Integer> adjacency = new Pair<>(edge_parent, edge_child);
                adjacencyList.add(adjacency);
            }

        // alphabetize componentList and preserve the mapping of the indices
        List<Component> alphabetizedList = new ArrayList<>(componentList);
        HashMap<Integer, Integer> indexMapping = new HashMap<>();
        alphabetizedList.sort(Comparator.comparing(o -> o.name));

        // iterate over alphabetizedList to get indices and construct mapping of old to new indices
        for (int i=0; i<alphabetizedList.size(); i++){
            Integer newIndex = i;
            Integer originalIndex = -1;
            for (int j=0; j<componentList.size(); j++){
                Component thisAtom = alphabetizedList.get(i);
                if (thisAtom == componentList.get(j)){
                        originalIndex = j;
                        break;
                }
            }
            // save the mapping
            indexMapping.put(originalIndex, newIndex);
        }

        // define a list of Compounds Nodes so we can access them by index
        // This is just a mapping of Atoms in alphabetizedList to compoundNode(Atom) in the nodesList.
        List<CompoundNode> nodesList = alphabetizedList.stream().map(CompoundNode::new).collect(Collectors.toList());

        // save the first compound in alphabetizedList as the root of the compound tree
        loadedCompound.root = nodesList.get(0);

        // iterate over adjacencyList and populate the adjacencies of each node
        for (Pair<Integer, Integer> adjacency : adjacencyList) {
            Integer parentIndex = indexMapping.get(adjacency.getKey());
            Integer childIndex = indexMapping.get(adjacency.getValue());

            // link the child to the parent and the parent to the child
            CompoundNode parentNode = nodesList.get(parentIndex);
            CompoundNode childNode = nodesList.get(childIndex);
            if (parentNode.adjacencies.contains(childNode)) {
                Integer weight = parentNode.adjacencyWeights.get(parentNode.adjacencies.indexOf(childNode));
                parentNode.adjacencyWeights.set(parentNode.adjacencies.indexOf(childNode), weight + 1);

                // assume the parentNode is also contained in the adjacencies of childNode
                childNode.adjacencyWeights.set(childNode.adjacencies.indexOf(parentNode), weight + 1);
            } else {
                parentNode.addAdjacency(childNode, 1);
                childNode.addAdjacency(parentNode, 1);
            }
        }

        // save the node list to the compound
        loadedCompound.nodeList = nodesList;
        return loadedCompound;
    }
}
