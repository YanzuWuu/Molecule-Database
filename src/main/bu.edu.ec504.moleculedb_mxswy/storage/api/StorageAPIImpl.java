package bu.edu.ec504.moleculedb_mxswy.storage.api;

import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class StorageAPIImpl implements StorageAPI {
	String FILENAME = "src/main/bu.edu.ec504.moleculedb_mxswy/storage/files/FileList.txt";
	ArrayList<CompoundFile> fileList = new ArrayList<>();
	int currentIndex;

	public StorageAPIImpl() {
		readInFileList();
	}
	
	/**
	 * Function for reading in the fileList file into filesList array
	 */
	private void readInFileList() {
		File file = new File(FILENAME);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		}
		catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		}
		currentIndex = scanner.nextInt();
		int numFiles = scanner.nextInt();
		for (int i = 0; i < numFiles; i++) {
			String filename = scanner.next();
			int nodes = scanner.nextInt();
			int compounds = scanner.nextInt();
			ArrayList<Integer> IDs = new ArrayList<>();
			for (int j = 0; j < compounds; j++) {
				IDs.add(scanner.nextInt());
			}
			CompoundFile nextFile = new CompoundFile(filename, nodes, compounds, IDs);
			fileList.add(nextFile);
		}
		scanner.close();
	}
	
	/**
	 * Function for saving data in filesList array
	 */
	private void outputFileList() {
		File file = new File(FILENAME);
		FileWriter writer;
		try {
			writer = new FileWriter(file, false);
			writer.write(currentIndex + System.lineSeparator());
			int numFiles = fileList.size();
			writer.write(numFiles + System.lineSeparator());
			for (CompoundFile compoundFile : fileList) {
				writer.write(compoundFile.getFilename() + " ");
				writer.write(compoundFile.getNumNodes() + " ");
				writer.write(compoundFile.getNumCompounds() + " ");
				ArrayList<Integer> IDs = compoundFile.getIDList();
				for (Integer id : IDs) {
					writer.write(id + " ");
				}
				writer.write(System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Function for saving compound to file
     * @param compound: Compound object with compound to store
     */
	public int saveCompound(Compound compound) {
		int numNodes = compound.nodeList.size();
		int numEdges = compound.countEdges();
		CompoundFile compoundFile = getFileFromList(numNodes);
		ArrayList<Integer> sameSizeIDs = getIDsForSameSizeCompound(numNodes, numEdges);
		boolean alreadyExists = false;
		for(int i = 0; i < sameSizeIDs.size(); i++){
			if(compound.isIsomorphic(getCompound(sameSizeIDs.get(i)))){
				alreadyExists = true;
				break;
			}
		}
		if(!alreadyExists) {
			int compoundID = currentIndex + 1;
			compoundFile.addCompound(compoundID, compound);
			currentIndex++;
			outputFileList();
			return compoundID;
		}
		return -1;
	}
	
	/**
     * Function for deleting compound from database
     * @param ID: The unique ID of the compound to delete
     */
	public void deleteCompound(int ID) {
		CompoundFile compoundFile = getFileFromID(ID);
		if(compoundFile != null) {
			compoundFile.deleteCompound(ID);
			outputFileList();
		}
	}

	/**
     * Function for returning compound based on given ID
     * @param ID: The unique ID of the compound to return
     * @return Compound
     */
	public Compound getCompound(int ID) {
		Compound compound = null;
		CompoundFile compoundFile = getFileFromID(ID);
		if(compoundFile != null){
			compound = compoundFile.readCompound(ID);
		}
		return compound;
	}

	/**
     * Function for finding compound ID's that match the number of Edges
     * and the number of nodes given
     * @param numNodes: The number of nodes to match in database
     * @param numEdges: the number of edges to match in database
     * @return ArrayList<Integers> returns a list of the matching compound ID's
     */
	public ArrayList<Integer> getIDsForSameSizeCompound(int numNodes, int numEdges) {
		ArrayList<Integer> IDs = new ArrayList<>();
		CompoundFile file = getFileFromList(numNodes);
		ArrayList<Integer> sameNodeIDs = file.getIDList();
		for (Integer sameNodeID : sameNodeIDs) {
			Compound compound = getCompound(sameNodeID);
			if (compound.countEdges() == numEdges) {
				IDs.add(sameNodeID);
			}
		}
		return IDs;
	}

	/**
	 * Function that finds all compound IDs in database with >= numNodes
	 * @param numNodes the number of nodes a compound must have for their ID to be returned
	 * @return Arraylist of valid compound IDs
	 */
	public ArrayList<Integer> getIDsForSameOrLargerSizeCompound(int numNodes){
		ArrayList<Integer> IDs = new ArrayList<>();
		for (CompoundFile compoundFile : fileList) {
			if (compoundFile.getNumNodes() >= numNodes) {
				ArrayList<Integer> possibleIDs = compoundFile.IDs;
				IDs.addAll(possibleIDs);
			}
		}
		return IDs;
	}

	/**
	 * Function for getting compoundFile corresponding to the given number of nodes
	 * Creates new CompoundFile if one does not exist of the right size
	 * @param numNodes number of nodes that the given file need to contain
	 * @return compoundFile where compounds with given number of nodes are stored
	 */
	private CompoundFile getFileFromList(int numNodes) {
		CompoundFile compoundFile = null;
		for (CompoundFile file : fileList) {
			if (file.getNumNodes() == numNodes) {
				compoundFile = file;
			}
		}
		if(compoundFile == null){
			compoundFile = createNewFile(numNodes);
		}
		return compoundFile;
	}

	/**
	 * Function for getting compoundFile with given compound ID in it
	 * @param ID ID of the compound being looked for
	 * @return CompoundFile containing compound of given ID
	 */
	private CompoundFile getFileFromID(int ID) {
		CompoundFile compoundFile = null;
		for (CompoundFile file : fileList) {
			if (file.checkIfIDPresent(ID)) {
				compoundFile = file;
				break;
			}
		}
		return compoundFile;
	}

	/**
	 * Function for creating new CompoundFile and txt file
	 * @param numNodes number of nodes the file is going to represent
	 * @return new CompoundFile initialized
	 */
	private CompoundFile createNewFile(int numNodes) {
		String filename = "src/main/bu.edu.ec504.moleculedb_mxswy/storage/files/CompoundFiles/Compounds_" + numNodes + ".txt";
		File file = new File(filename);
		try{
			file.createNewFile();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		ArrayList<Integer> IDs = new ArrayList<>();
		CompoundFile newFile = new CompoundFile(filename, numNodes, 0, IDs);
		fileList.add(newFile);
		outputFileList();
		return newFile;
	}
}
