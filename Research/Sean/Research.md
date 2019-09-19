# Research Notes

##Types of Databases

	Relational - SQL database stores data in predefined tables with specified 
				attributes for each column
				
				
	Non-Relational - NoSQL database where the data is stored with some other relation scheme 
					other then the tabular on used in a relational database.
		


##Storage File Format

	ASCII - Text files offer readability and formatting 
			at the cost of size and speed efficiency
		
	Binary - Binary files offer size and speed effieciency 
			at the cost of readability.
		
	XML - XML files allow for formatting of data in groups and
			structure within the file at the cost of size and
			and speed efficiency
		
		
		
		
##Features

	Ability to handle core operations on 10,000 complex molecules, each of 
	at least 10 thousand atoms, at a rate of 10 operations per second on a lab computer. [10%]
	
		Focuses on the specific compression and accesability of very large molecules.
	
	
	Ability to search for the most similar molecule to a given molecule,
	under some reasonable graph similarity metric. [10%]
	
	
	Implement subgraph search - finding all molecules that contain a subgraph provided. [10%]
	
	
	Implement approximate subgraph search - finding all molecules that contain
	a molecule similar to the given molecule (with respect to a tunable parameter
	based on a reasonable graph similarity metric). Note that a solution for this
	element is also a solution for the subgraph search element, and the contributions add. [10%]

		These three focus on the way the molecules are stored and how the search function has to either look
		every entry or just ones that fall in some classification
		