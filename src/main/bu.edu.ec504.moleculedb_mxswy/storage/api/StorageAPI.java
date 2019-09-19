package bu.edu.ec504.moleculedb_mxswy.storage.api;

import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;

import java.util.ArrayList;

public interface StorageAPI {
	
	/**
     * Function for saving compound to file
     * @param compound: Compound object with compound to store
     */
	int saveCompound(Compound compound);
	
	/**
     * Function for deleting compound from database
     * @param ID: The unique ID of the compound to delete
     */
	void deleteCompound(int ID);
	
	/**
     * Function for returning compound based on given ID
     * @param ID: The unique ID of the compound to return
     * @return Compound
     */
	Compound getCompound(int ID);

	/**
     * Function for finding compound ID's that match the number of Edges
     * and the number of nodes given
     * @param numNodes: The number of nodes to match in database
     * @param numEdges: the number of edges to match in database
     * @return ArrayList<Integers> returns a list of the matching compound ID's
     */
	ArrayList<Integer> getIDsForSameSizeCompound(int numNodes, int numEdges);

	/**
	 * Function that finds all compound IDs in database with >= numNodes
	 * @param numNodes the number of nodes a compound must have for their ID to be returned
	 * @return Arraylist of valid compound IDs
	 */
	ArrayList<Integer> getIDsForSameOrLargerSizeCompound(int numNodes);
	
}
