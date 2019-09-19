package bu.edu.ec504.moleculedb_mxswy.search;

import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;

import java.util.List;

public interface SearchAPI {
    /**
     * Function for finding a compound by name given a Compound object
     *
     * @param compoundData : Compound object loaded from file content
     * @return String compoundID or "Not Found"
     */
    List<Integer> byName(Compound compoundData);

    /**
     * Function for finding a compound by chemical formula given a Compound object
     *
     * @param compoundData : Compound object loaded from file content
     * @return Arraay of String compoundID or "Not Found"
     */
     List<Integer> byChemicalFormula(Compound compoundData);

    /**
     * Function for finding a compound by isomorphism given a Compound object
     *
     * @param compoundData : Compound object loaded from file content
     * @return String compoundID or "Not Found"
     */
    List<Integer> byIsomorphism(Compound compoundData);

    /**
     * Function for finding a compound up to subgraph isomorphism given a Compound object
     *
     * @param compoundData : Compound object loaded from file content
     * @param searchMode : "exact" or "fuzzy" for types of subgraph search
     * @return String compoundID or "Not Found"
     */
    List<Integer> bySubgraph(Compound compoundData, String searchMode);
}
