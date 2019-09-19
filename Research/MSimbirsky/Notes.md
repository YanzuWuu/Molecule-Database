# Research Notes

Types of Databases:
* Relational (SQL) Databases
* Graph (No-SQL) Databases
* Object Oriented Databases

| Database model | Abstraction level | Base data structure | Information focus |
| --- | --- | --- | ---| 
Network | physical|  pointers + records | records
Relational | logical | relations | data + attributes
Semantic | user | graph | schema + relations
Object-Oriented | physical/logical | objects | object + methods
Semistructured | logical | tree | data + components
Graph | logical/user | graph | data + relations

## Relational Databases (SQL)

Consist of tables (2D Arrays)
Data typically needs to be normalized, meaning attributes 
of databases objects are linked across tables instead of 
entries existing in multiple places. This means there's no data
duplication, so updating and deleting data is a single operation
and the objects take up less storage. However, looking up data
across tables is costly because joins are expensive.

The relational model is geared towards simple record-type data, where the
data structure is known in advance (airline reservations, accounting, inventories, etc.).
The schema is fixed, which makes it difficult to extend these databases. It is not easy to
integrate different schemas, nor is it automatable. The query language cannot explore
the underlying graph of relationships among the data, such as paths, neighborhoods,
patterns.

Pros:
* Easy and quick to look up any part of the table
* Random access

Cons:
* No native support for graph structures
* Undirected multigraphs would have to be stored carefully and queries written carefully to access

## Object Oriented Databases

Data is represented as a collection of objects that are organized into classes, and have
complex values and methods. Although O-O db-models permit much richer structures
than the relational db-model, they still require that all data conform to a predefined
schema [Abiteboul et al. 1997].

## Graph Databases

Graph Databases are a type of no-SQL databases that is natively written for storing and accessing graph objects. Most
graph databases focus on global graphs, such as social networks where all vertices are linked to each other. However, 
we're looking for a data structure to store a transactional graph, meaning each molecule adjacency matrix stands 
independently and is not linked to other molecules.

### Transactional Graph Databases

A transactional graph database is represented
as a finite set D = {g1, g2,...,gn} of n graph structures, where each graph gi represents a transaction in the scope of 
a dataset. For example, a chemical compound dataset [Yan and Han, 2002], can be conceived as a transactional graph database,
where the structure of each chemical compound is represented as a graph g.

### Global Graph Databases

A single large graph database is composed of one
large connected graph, where vertices and edges represent the entities and the relations among them for the entire dataset. For example, a social network or the World
Wide Web can be conceived as single large graphs [Boden et al., 2012, Zou et al.,
2014b]. 


Note: Graph databases are not a good fit for versioning data sets or keeping an audit trail of changes. Graph database 
vendors don’t have native support for time based versioning and the query languages don’t have built in support.


# Features

Required features:
* Ability to work with at least 10,000 molecules.
* Ability to search for a given molecule up to graph isomorphism ( i.e., molecules that have the same graph structure, even if their text representation is different).

Graphs should be indexed or normalized so isomorphic graphs are identical

* Command-line User Interface 

CLI should be a simple wrapper around the API we develop

* The database should be able to handle at least 10 operations per second on a 10,000 molecule database on a lab computer.

Features of interest:
* Run a server for your database than can be accessed through some reasonable interface over a socket port. [5%]

This can be achieved through any database structure as long as it has a reasonable API

* Develop a web page and corresponding server-side executable for accessing the database. [5%]

This is a trivial extension of #1.
*  Implement subgraph search - finding all molecules that contain a subgraph provided. [10%]

Data would need to be stored in such a way that relational searches can be performed directly, without loading molecules 
into memory. This rules out relational and object oriented databases.

* Download 1,000 known compounds from an existing database (e.g., [http://www.chemspider.com/AboutServices.aspx?][ChemSpider]]) into your molecular database. [5%]

This is a "freebie" since it seems we can get input data from here.
