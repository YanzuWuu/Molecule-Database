4 features:
1.	Develop a stand-alone Graphical User Interface that provides molecular entry and search capabilities, database statistics, and also displays molecules in some reasonable graphic format. [5%]
2.	Download 1,000 known compounds from an existing database (e.g., [http://www.chemspider.com/AboutServices.aspx?][ChemSpider]]) into your molecular database. [5%]
3.	Implement subgraph search - finding all molecules that contain a subgraph provided. [10%]
4.	Implement approximate subgraph search - finding all molecules that contain a molecule similar to the given molecule (with respect to a tunable parameter based on a reasonable graph similarity metric). Note that a solution for this element is also a solution for the subgraph search element, and the contributions add. [10%]
5.	(Alternative)Ability to handle core operations on a database of at least 10 million molecules at a rate of 10 operations per second on a lab computer. [10%]

SQL database:
1. Expansion difficulties: Due to the existence of a multi-table query mechanism like Join, the database is very difficult to expand;
2. Slow reading and writing: This situation mainly occurs when the amount of data reaches a certain scale. Because the system logic of the relational database is very complicated, it is very prone to concurrency problems such as deadlock, so the read and write speed of the relational database is very serious.
3. Limited support capacity: Existing relational solutions are not able to support such massive data storage as Google;

NoSQL database:
1. Simple extension: A typical example is Cassandra. Since its architecture is similar to classic P2P, it can be extended by easily adding new nodes.
2. Fast read and write: Due to its simple logic and pure memory operation（and delete relationships?）, its performance is very good. A single node can process more than 100,000 read and write operations per second.
3. NoSQL is more flexible，In the document you can store any form of data, data can be added very flexibly. It is not necessary to design the fields and tables in advance.


Implementation difficulty of NoSQL:
The NoSQL database uses the same query as the javascript language with the json parameter. The basic operation is simple, but for more complex queries, nested JSON can be very complicated.
While SQL is a lightweight, interpreted language. The grammar is powerful and has become an international standard. Although most systems implement grammars slightly differently.

Implementation difficulty of SQL:
When we store different molecule with different numbers of value, it may be difficult to change the table.


Tendency choice：NoSQL database
In our case, we may don’t know how many values does a molecule data has, and we also have to pursue a faster speed, even regardless about the 5th features I chose, due to the comparative features. So, NoSQL may be a better choice.  






