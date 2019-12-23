My program can be run with the following on the command line:

java -jar DistanceVectorCalculator-1.0.jar

It will output the shortest path matrix for the specified network graph, specifically it will determine the shortest distance between any two nodes in the network and represent this information in the form of an adjacency matrix.

To specify a network graph for the program, please use an adjacency matrix, like so:

	0	2	0	0	1
	2	0	5	0	0
	0	5	0	4	0
	0	0	4	0	1
	1	0	0	1	0 

The above matrix represents this graph: https://gyazo.com/c087dd9514382b70f0fd8c736556b66f

Ensure the network.txt file is in the same directory as the jar file before running.

To build my program, you must use Apache Maven
To do so ensure you are inside the DistanceVectorCalculator directory and run "mvn clean install".
The compiled .jar file will be output in the target directory.

Source files for the program are located in: "DistanceVectorCalculator/src/main/java/PA2/"
