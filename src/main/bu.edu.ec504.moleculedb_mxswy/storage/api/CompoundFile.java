package bu.edu.ec504.moleculedb_mxswy.storage.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import bu.edu.ec504.moleculedb_mxswy.storage.model.Compound;
import bu.edu.ec504.moleculedb_mxswy.storage.model.CompoundNode;
import bu.edu.ec504.moleculedb_mxswy.storage.model.Component;

public class CompoundFile {
	
	String filename;
	int numNodes;
	int numCompounds;
	ArrayList<Integer> IDs;
	
	public CompoundFile(String file, int nodes, int compounds, ArrayList<Integer> ID) {
		filename = file;
		numNodes = nodes;
		numCompounds = compounds;
		IDs = ID;
	}
	
	/**
     * Function for checking whether an ID is in file
     * @param ID: the ID being searched for
     * @return boolean representing whether ID is present or not
     */
	public boolean checkIfIDPresent(int ID) {
		boolean result = false;
		for(int i = 0; i < IDs.size(); i++) {
			if(ID == IDs.get(i).intValue()) {
				result = true;
			}
		}
		return result;
	}
	
	/**
     * Function for adding information about compound to given file
     * @param compound: The compound object to be added
     * @param ID: ID being added to file
     */
	public void addCompound(int ID, Compound compound) {
		File file = new File(filename);
		FileWriter writer;
		try {
			writer = new FileWriter(file, true);
			writer.write(ID + " ");
			writer.write(compound.name);
			for(int i = 0; i < numNodes; i++) {
				CompoundNode node = compound.nodeList.get(i);
				writer.write(" " + node.component.name.trim());
				for(int j = 0; j < node.adjacencies.size(); j++) {
					CompoundNode adjNode = node.adjacencies.get(j);
					int adjIndex = compound.nodeList.indexOf(adjNode);
					if(i > adjIndex){
						for(int k = 0; k < node.adjacencyWeights.get(j); k++) {
							writer.write("_" + adjIndex);
						}
					}
				}
			}
			writer.write(System.lineSeparator());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		numCompounds++;
		IDs.add(ID);
	}
	
	/**
     * Function for removing information about compound to given file
     * @param ID: ID being removed to file
     */
	public void deleteCompound(int ID) {
		ArrayList<Integer> IDList =  new ArrayList<Integer>();
		ArrayList<String> compoundStr = new ArrayList<String>();
		File file = new File(filename);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		}
		catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < numCompounds; i++) {
			int currID = scanner.nextInt();
			if(currID != ID){
				IDList.add(currID);
				compoundStr.add(scanner.nextLine());
			}
			else{
				scanner.nextLine();
			}
		}
		scanner.close();

		numCompounds--;
		IDs.remove(Integer.valueOf(ID));

		FileWriter writer;
		try {
			writer = new FileWriter(file, false);
			for(int i = 0; i < numCompounds; i++) {
				writer.write(IDList.get(i) + compoundStr.get(i) + System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function for removing information about compound to given file
	 * @param ID: ID of compound being read from file
	 */
	public Compound readCompound(int ID) {
		File file = new File(filename);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		}
		catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		}
		int currID = 0;
		String name = null;
		String compoundStr = null;
		for(int i = 0; i < numCompounds; i++) {
			currID = scanner.nextInt();
			name = scanner.next();
			compoundStr = scanner.nextLine().trim();
			if(currID == ID){
				break;
			}
		}
		scanner.close();
		Compound compound = new Compound(name);

		ArrayList<String[]> nodeList = new ArrayList<String[]>();

		String nodes[] = compoundStr.split(" ");
		for(int i = 0; i < numNodes; i++){
			nodeList.add(nodes[i].split("_"));
		}

		//Add all nodes to compound
		for(int i = 0; i < numNodes; i++){
			Component newComponent = new Component(nodeList.get(i)[0]);
			CompoundNode newNode = new CompoundNode(newComponent);
			compound.nodeList.add(newNode);
		}

		//Add all edges to compound
		for(int i = 0; i < numNodes; i++){
			CompoundNode currNode = compound.nodeList.get(i);
			String[] edgeArray = nodeList.get(i);
			for(int j = 1; j < edgeArray.length; j++){
				CompoundNode adjNode = compound.nodeList.get(Integer.parseInt(edgeArray[j]));
				currNode.addAdjacency(adjNode, 1);
				adjNode.addAdjacency(currNode, 1);
			}
		}

		return compound;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public int getNumNodes() {
		return numNodes;
	}
	
	public int getNumCompounds() {
		return numCompounds;
	}
	
	public ArrayList getIDList() {
		return IDs;
	}
	
}
