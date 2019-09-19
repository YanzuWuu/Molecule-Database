package bu.edu.ec504.moleculedb_mxswy.api;

import bu.edu.ec504.moleculedb_mxswy.search.SearchTypes;
import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;

import java.io.IOException;
import java.util.List;

public interface CompoundService {
    /**
     * Function for finding a compound up to isomorphism given a Compound object
     *
     * @param compoundData: Compound object loaded from file content
     * @return String compoundName or "NAN" for "Not Found"
     */
    List<Integer> findCompound(Compound compoundData, SearchTypes searchType);

    /**
     * Function for saving a new compound in the database from a Compound object
     * @param compoundData: Compound object loaded from file content
     */
    int saveCompound(Compound compoundData);

    /**
     * Function for deleting a compound from the database using the compound name or unique identifier
     * @param compoundID: String containing unique compound identifier
     */
    void deleteCompound(int compoundID);

    /**
     * Function for updating or overwriting an existing compound with new data using the compound name and the new data.
     * @param compoundID: String containing unique compound identifier
     * @param newData: Compound object loaded from file content to overwrite existing stored Compound.
     */
    void modifyCompound(int compoundID, Compound newData);


    /**
     * Function to load a specific ID from PubChem into the database
     * @param ID: PubChem ID of target molecule
     */
    void pubchemLoadID(int ID) throws IOException;

    /**
     * Function to load a random number of compounds from PubChem into the database
     * @param numMolecules: Number of molecules to load into the database.
     */
    void pubchemLoadRandom(int numMolecules) throws IOException;

    /**
     * Helper function to convert text file contents to a Compound object
     * @param fileContent: The contents of a text file describe a molecule adjacency matrix
     * @return Compound object
     */
    Compound loadFromFile(String fileContent);
}
