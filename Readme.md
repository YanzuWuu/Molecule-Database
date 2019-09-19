# Molecule DB MXSWY Project

## Project Description

This project implements a database for computational chemistry that can efficiently store molecule data in the form of
adjacency list representation, which supports operations with more than 10,000 molecules and isomorphic graph search. Due to
chemical compounds being represented as undirected graphs with no self-loops, a graph database had to be implemented for this project as
the problem does not naturally translate to a table-based SQL-like database.

In this project, the database schema, search functions, and storage format were all implemented from scratch. Additionally we
implemented a Commandline interface and a WebServer REST API with a corresponding WebUI to access the database.

For all interactions with the database, molecule input files have the following format:
```
[MOLECULE NAME]
[# OF VERTICES]
[LABEL OF VERTEX ID 0]
[LABEL OF VERTEX ID 1]
[LABEL OF VERTEX ID 2]
...
[LABEL OF VERTEX ID V]
```

followed by edges represented as:
```
[VERTEX ID a] [VERTEX ID b]
```

As an example, water looks as follows:
```
Water
3
H
H
O
0 2
1 2
```

### Features

The list of features this database supports are as follows:

Minimum Features:
* Ability to work with at least 10,000 molecules.
* Ability to search for a given molecule up to graph isomorphism ( i.e., molecules that have the same graph structure, even if their text representation is different).
* Command-line User Interface to access the database
* The database should be able to handle at least 10 operations per second on a 10,000 molecule database on a lab computer.

Additional Features:
* Develop a web page and corresponding server-side executable for accessing the database. [5%]
* Run a server for your database than can be accessed through some reasonable interface over a socket port. [5%]
* Download 1,000 known compounds from an existing database into your molecular database. [5%]
* Ability to handle core operations on a database of at least 10 million molecules at a rate of 10 operations per second on a lab computer. [10%]
* Implement subgraph search - finding all molecules that contain a subgraph provided. [10%]
* Implement approximate subgraph search - finding all molecules that contain a molecule similar to the given molecule (with respect to a tunable parameter based on a reasonable graph similarity metric). Note that a solution for this element is also a solution for the subgraph search element, and the contributions add. [10%]

### Implementation

* Source is found under src/main/bu.edu.ec504.moleculedb_msxwy


* Under the api directory, there is a CompoundService interface and implementation. The CompoundService is where the database is
initialized and is used to interface with all internal components of the database. The CompoundService initializes StorageAPI and SearchAPI. We implemented the Database in a modular format
so that group members can work on different modules simultaneously with integration handled in CompoundService.


* The storage/model contains a definition of how Compounds are parsed from raw text files into a Java object. The Compound
class consists of a nodeList, which is an Array of compoundNodes and represents the atoms in the compound. The Compound is represented as an 
undirected graph with no self loops by linking each node to its adjacent nodes. Each CompoundNode in Compound.nodeList is a Java object composed 
of the node name (the atom it represents), the adjacency list of the node, and the corresponding adjacency weights of each adjacency. The adjacencyList of each node is an Array of 
nodes to which this node has an edge. This list is unique and each node appears only once even if there are multiple edges 
present to that node. The adjacency weights are an array of integers which represent how many times that adjacency appears, meaning how many edges are present between each pair of nodes.
The adjacencyList and adjacencyWeights arrays are of the same length and have the same indices.


* The Main() function uses SparkJava to run a lightweight REST API which communicates with socket port 8080. It provides 
routes to receive requests via POST HTTP requests which send a JSON body with the request info, typically the compoundData 
or compoundID of the search query. Before initializing HTTP routes, it also initializes CompoundService(), which in turn initializes Search and Storage APIs. This implements the feature "Run a server for your database 
than can be accessed through some reasonable interface over a socket port.". In addition, the base route "/\*" renders a static HTML/javascript page which provides a WebUI for the database.
Each field in the WebUI sends an HTTP request using a JSON POST request to the server and displays the returned response. This implements the feature "Develop a web page and corresponding server-side executable for accessing the database."


* The saving of compounds is done by first removing redundant data from the Compound model we have defined such as both directions of the edge and implicity saving the start and end of each edge. The simplified compound is then stored in a file from a list of files that contains only compounds with the same number of nodes. There is then a header file which contains information about each of the compound files.


* The SearchAPI is located in src/main/bu.edu.ec504.moleculedb_msxwy/search/SearchAPI. The base search function is Isomorphism search. For two compounds to be isomorphic, they must have the same number of nodes and 
their nodes must map to each other in a 1-to-1 mapping. This is done by iterating over the nodes and establishing a mapping, meaning for an isomorphic pair of molecules, the worst case 
runtime to show they are isomorphic is O(n^2). Two nodes are considered to be equal and therefore map to each other if they have the same name, same number of adjacencies, and their adjacencies also have a mapping to each other. 
Thankfully, the worst case runtime is reached only when compounds are exactly isomorphic. As soon as a single difference is detected, the compounds are determined to not be isomorphic. This makes the Amortized analysis of the runtime O(n).
Only compounds which have the same number of nodes, edges, and chemical formula are checked for Isomorphism during Isomorphism search. Filtering of candidate compounds is performed by the StorageAPI.


* In addition to Isomorphism search, the Search API provides search by Name, search by Chemical Formula and search by Subgraph. The candidate compounds for the name and the chemical formula 
search are similarly filtered by the Storage API to only check compounds which have the same number of nodes and edges and the same chemical formula as the input compound. Subgraph search similarly filters
the candidate search compounds to only compounds which have the same or greater number of nodes as the input compound. After the candidate compounds are found, the search functions iterate over the candidates and return a list of matches.


* Approximate Subrgraph Search is implemented using an algorithm that goes through every permutation of mapping the nodes in the subgraph to nodes present in the graph. The permutation grows starting from one node mapped and checking it then adding a second checking a continue this. This is done recursivly and when the checks are incorrect the last added node to the permutation is poped off the list and the next possibility is added. The Tunnable parameter for the similarity metric is based on the number of different edge weights and node values. Each difference is +1 for the metric. A similarity metric of 2 would allow for up to two different nodes, two different edges, or one of each. The value of how different the subgraph is from the graph is checked every time the mapping check is run and havin a difference greater than the given is also a failed check.


* Subgraph Search is done exactly the same as Approximate Subgraph Search except the similarity metric is set to 0 so any differences fail that potential permutation.


* For Command-line User Interface, the Apache Commons CLI library is used. It provides an http request to server for every command, and then return the result on terminal. 
  The main procedure is: 1. Add command options like "modifyMolecule" and give discription of it; 2.Get the input as string values when one specific option is detected; 3. Create a JSON object with the given request and make a POST HTTP request to the server with the input data and corresponding HTTP path 
  and parse the response.; 4.Parse the command options and return the result.
  

* To demonstrate the ability to handle core operations, we implemented a CompoundGenerator class. This class initializes an empty Compound object and creates randomly generated nodes in this compound 
according to the number of nodes specified by the user. The generator keeps track of any nodes which have no edges connected to them an ensures all nodes are connected. It also generates a random number of edges for each node. 
Once the compound is complete, it writes the output compound to a text file. To populate the database, we rendered 10,000 generated compounds and used the Commandline interface to load these into
the database. We then ran SystemsTests which perform base operations and demonstrated that 10 operations can be run per second on the database.


* Since the Chemspider has 1000 download request per monthly limit, the group decide to download compound data from the PubChem online database. This part is implement with the python script. 
Using python script to connect the Pubchem database, and depend on the provided CID to download the Compound and save as text file with expected format. The download data include compound name, compound element and the chemical bonds between element.
The script can complete two type of download, random download and specific download. The specifc download can acquire specific compound data with its CID (CID is unique Id for each compound in Pubchem database). The random download method with random generate a list of CID and download amound compound data in one operation.
Also created DownloadDataProcess java class to handle these download data. The moveToDatabase function can check the download folder,  and transfer all the text file data to the database with storage format. The loadFromDownload function can search the specif compoud with name in Download folder.


* The system test implement with JUnit 5.3 library. System test create a series of test corresponding to the Compound service and check each basic operations  work as expectation. For each test, given some desired input and the return output should be equal with expectation result.
 If the result is not the expectation,  the group need to go through all the functions to debug  and make sure all the api connect without error.
 The System test also include the speed test and that can check all the basic operation finish 10 time in 1 second in a 10000 molecule database.


### References

1. Survey of graph database models: https://www.researchgate.net/publication/40884504_Survey_of_graph_database_models.
2. Lena Wiese. Advanced Data Management: For SQL, NoSQL, Cloud and Distributed Databases, Chapter 4: Graph Databases (page 41): https://books.google.com/books?id=YKMLCwAAQBAJ&lpg=PA49&ots=MND50H9qN6&dq=undirected%20multigraph%20to%20database%20row&pg=PA41#v=onepage&q&f=false.
3. J. R. Ullmann, An Algorithm for Subgraph Isomorphism: https://dl.acm.org/citation.cfm?id=321925.
4. Luigi P. Cordella, Pasquale Foggia, Carlo Sansone, Mario Vento. A (Sub)graph Isomorphism Algorithm for Matching Large Graphs: http://ieeexplore.ieee.org/iel5/34/29305/01323804.pdf.
5. L. P. Cordella, P. Foggia, C. Sansone, M. Vento. An Improved Algorithm for Matching Large Graphs: https://pdfs.semanticscholar.org/f3e1/0bd7521ec6263a58fdaa4369dfe8ad50888c.pdf.
6. Bonnici, Vincenzo, et al. “A Subgraph Isomorphism Algorithm and Its Application to Biochemical Data.” BMC Bioinformatics, vol. 14, no. Suppl 7, 2013, doi:10.1186/1471-2105-14-s7-s13.
7. Cormen, Thomas H.., et al. Introduction to Algorithms. MIT University Press, 2009.
8. Alan Gibbons, Algorithmic Graph Theory. Cambridge University Press, 1985.
9. “PubChemPy Documentation.” PubChemPy Documentation - PubChemPy 1.0.4 Documentation, pubchempy.readthedocs.io/en/latest/.
10. Kim, Sunghwan, et al. “PubChem 2019 Update: Improved Access to Chemical Data.” Nucleic Acids Research, vol. 47, no. D1, 2018, doi:10.1093/nar/gky1033.
11. Davoodi, Mohammed. Configuring Webhook To Jenkins for Bitbucket. Atlassian, 19 Mar. 2018, mohamicorp.atlassian.net/wiki/spaces/DOC/pages/381419546.
12. "SparkJava Documentation." SparkJava 2.8.0 Documentation. http://sparkjava.com/documentation

## Group 10 Members

**Maria Simbirsky** is a BME M.S. student with expected graduation in Spring 2019. She is a bioinformatician with over 5
years of industry experience. She is a part-time student completing on her M.S. while working full time.

**Wanxuan Chen** is a ECE M.S. student, Expected graduation in Fall 2019.

**Sean Nonnemacher** is a ECE M.S. student, Expected graduation in Fall 2019.

**Yuncheng Zhu** is a ECE M.S. student with expected graduation in Spring 2020.

**Xin Li**

## Source Code

Project source code is located in the "src" directory. The "main" directory should be specified as "Sources Root" and 
the "test" directory should be specified as "Test Sources Root"

## Dependencies

* Python3 must be installed and callable by running `python`.
* For PubChem functions, the module "PubChemPy" must be installed. This can be installed by running `pip install pubchempy`
* All other dependencies are handled through Maven. Please see INSTALL.txt for instructions on how to run the Maven dependency builder.

## Work Breakdown

### Maria Simbirsky
* Project management: assigning tasks, creating JIRA tickets, organizing meetings, minor bug fixes and code style 
changes, and general guidance through the duration of the project.
* Implemented the primary API: CompoundService interface and implementation
* Implemented the Storage model for Compound class, nodes, and Isomorphism search
* Implemented the CompoundGenerator class
* Implemented the basic Search API functions, including search by name, search by isomorphism and search by chemical formula.
* Implemented the web server "Main.main()" runner and the corresponding WebUI

### Wanxuan Chen
* Implemented systems and speed tests for the application
* Debugging to ensure all tests are passing
* Implemented the PubChem python scripts and corresponding Java class

### Sean Nonnemacher
* Performed code reviews and checked code style
* Implemented the Storage api and file management for how data is compressed and stored in the database
* Implemented data serialization/deserialization
* Implemented Subgraph and Approximate Subgraph Search
* Implemented database initialization

### Yuncheng Zhu
* Implemented systems and speed tests for the application
* Debugging to ensure all tests are passing
* Implemented the CLI class
* Debugging and improvements to the PubChem class


### Xin Li
* Did not make a significant contribution to the project
