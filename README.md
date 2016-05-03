# SocialNetworkAnalyzer
Providing some methods to analyze a Social Network.

Overview:

This project investigates friend relationships and influence within a social network. The first part of the project will
look at a friend’s friends and find those that aren’t connected for potential friend suggestions. The second part, the goal
will be to identify the most influential person(s) within a network by measuring each friend’s degree, closeness and
betweenness centrality.  

Centrality of nodes or the identification of which nodes are more “central” than the others has been a key issue for network
analysis. There are many types of ways to identify the centrality of nodes which can help determine the most influential
person of a network. For this project I will focus on three types of centrality as noted above.  For degree, this is the
simplest of the measures based on the number of ties/connections a person has. This can help determine popularity. For
closeness, this is based on the distance between all paris of nodes\friends. The intent behind this measure is the identify
the nodes\friends which could reach others quickly. For betweenness, this is based on the number of times a node\friend
appears or acts as a bridge along the shortest path between two other nodes. This can help to determine the person which has
the most communication flow between communities and is considered a better determination of influence than the other
centrality values noted. 

Data:

Example of a small network represented by integer ids per friend node. 

									40
								/  	| 	 \
							/   	| 		 \
				20	—-	30					60
				|		    \ 		|		/	
				|		     \		|	  /
				|				\		/
				10					50	

Question 1:

	For a given person, which of their friends aren't connected as friends? Those that aren't connected we will suggest them
	as potential friends.

Question 2: 

	For a given network, find the most influential person(s) based on their degree, closeness and betweenness centrality. 

Answer to the questions:

Question 1:

For the friend node id of 40, provide friend recommendation between its unconnected links\friends are:
 for friend 60, 30 is a suggestion,
 for friend 30, 60 is a suggestion. 

Question 2:

The most important\influential person based on degree are friend node ids of 30, 40 and 50 with degree value of three.
For importance based on closeness is friend node id of 30 with closeness value of 0.714.
For importance based on betweenness is friend node id of 30 with betweenness value of 12. 

As such, 30 is the most influential person within this network.  It has the most importance out of the three centrality
values. One thing of notice is the closeness values for ids 50 and 40 is the second highest at 0.625 higher than node id 20
which has 0.555.  Friend node ids 40 and 50 should be less important than 20 as if you get ride of either 40 or 50 only, a
path still exists between 10 to 60.  But if you remove node id of 20, 10 is no longer connected to anybody. Betweenness
calculation is used to show better ranking of the nodes and as such node id of 30 is first with betweenness value of 12 and
node id of 20 is second highest with betweenness value is 7. 

Algorithms, Data Structures, and Answer to the questions:

Important Data Structures:

	•	FriendGraph - This structure will represent a classic graph using an adjacency list.  The graph is undirected and unweighted. 
	•	FriendNode - This structure represents an individual as a node (vertex) within a graph. This node stores outgoing edges only as this is an undirected graph. 

Algorithm for question 1:

1. Pseudocode for SuggestFriendOfFriends(FriendNode person) method:

		Input: Specific person (u)
		Output: returns a list of persons with a list of friend recommendation(s) per person. 	
		Create a list of friends (vertices) for u	
		Create a return hash map for output
		
		For each friend in the list:
			For each friend in the list:
					if (outer for loop friend != the inner for loop friend and outer friend is not already a friend with inner friend)
						add pair <inner, outer> to the return hash map

		Return the return list.

Algorithms for question 2:

Measuring degrees of centrality is straight forward. This is calculated by tracking the amount of connections for each node.
This is easily maintained by the list of connections data structure stored in the FriendNode data structure and requesting
its size when determining degree. 

1. The method measureAndSetClosenessCentrality() measures the closeness centrality and saves it within each friend node. 

	Measure for each friend node how far it is from the rest of the friend nodes. This is done by finding all the shortest
	path length to each node in the network by 
	performing a BFS search. Performs a reverse division to provide a smaller average and high neutrality for closeness
	calculation, amount of paths/sun of all lengths.

	Pseudocode for measureAndSetClosenessCentrality():
	
			Create a hash map to store shortestPathLength and initialize it with all friend nodes as the key and value as -1
			
			For each friend node:
						add shortest path hash map with 0 for current length
						call helper measureAllShortestPathLenghts() method sending the friend node and shortestPathLength
						summarize all the lengths within shortestPathLength that don’t have -1 value
						set closenessCentrality of this friend node to the value = amount of paths/all lengths sum

	Pseudocode for measureAllShortestPathLenghts(FriendNode friendStartNode, shortestPathLength):
	
	
			Create a HashSet of friend node as “visited” to track already visited nodes
			Create a Queue of friend node as “toExplore” which is used as a queue for BFS searching

			add friendStartNode to the queue
			while toExplore is not empty:
							remove first element from the queue and set it to current friend node
							from current friend node set current distance from shortestPathLength map
							for all of friend node’s connections\edges
								If visited contains current edge connection friend node “continue” to the next edge connection 
  							otherwise add current connection friend node to the queue
								and add edge to visited. 
								set it with current distance + 1 within shortestPathLength map
			add current friend node to visited

2. The method measureAndSetBetweennessCentrality() measures the betweenness centrality and saves it within each friend node. 

	Measure for each friend node how many shortest path(s) are their between a pair of friends. How many of these shortest path(s) contain the current friend node? 
	Calculating the unweighed betweenness centrality using Brandes’ Algorithm.  It takes a different approach compare to the brute force way of summary up all the 
	pair-wise dependences. Brande calculates betweenness based on a dependency accumulation technique which accesses the nodes in reverse order of the BFS 
	traversal.  Detailed description of the algorithm and pseudocode that I followed is located at the following reference sites:

	http://algo.uni-konstanz.de/publications/b-fabc-01.pdf
	http://www.cc.gatech.edu/~bader/papers/FastStreamingBC-SocialComputing2012.pdf 

3. The method exportTopDegreeGraphs(int number): For a given network, return a list of subgraphs of a graph that include the most (M) influential based on degree centrality. 

	Pseudocode:
	
		Input: M=number value 
		Create a list of graphs to fill in for output
		Create a key value pair “data” hashmap structure to help with duplicates (friends with the same num of friends), key = connection size, value = a list of vertices. 

		call helper method sortFriendsBy(data, “degree”)

		For each sorted size up to the M value O(m)
   			retrieve the vertices for the current size O(n)
    			For each vertices
       				add edge to the graph list
		total runtime, O(m*n)

		return graph

4. The method returnTopCentralityFor(int number, String type): For a given network, return a list of friend nodes that includes only the most (M) influential based on centrality type.

	Pseudocode:
	
		Input: M=number value
		Input: T type
		Create a list of friend nodes to fill in for output
		Create a key value pair “data” hashmap structure to help with duplicates (friends with the same num of friends), key = connection size, value = a list of vertices. 

		call helper method sortFriendsBySize(data, T = “degree” or “closeness” or “betweenness”)

		For each sorted size up to the M value O(m)
    			retrieve the vertices for the current size 
    			For each vertices O(n)
       				add friend node to the list
		total runtime, O(m*n)

		return list of friend nodes.

5. The method sortFriendsBy(HashMap<Double, ArrayList<FriendNode>> data, String type): 

	 For the current network, take all of its vertices (friends), runtime O(n), and retrieve the type value (degree, closeness, or betweenness)
	 for each vertex and store the type value as a key within “data” and value as the vertex ID to the value list, runtime O(1).
	 Sort the sizes in the return list data structure in descending order, runtime O(nlogn) 

Algorithm Analysis:

Algorithm for question 1: 

	Brute force approach seems to be the most efficient way to go about this with question. Runtime is
	O(|V|2) for this algorithm. All other activities in the algorithm perform in O(1) constant time thanks to the usage of
	HashMap structure. 

Algorithms for question 2 for closeness centrality algorithm 1: 

	Measuring all the paths lengths runs in O(V) times (the
	number of vertices) within helper method measureAllShortestPathLenghts used in the main closeness centrality method within
	a for loop. Measuring closeness centrality takes O(V)+O(V) times to initialize a hash map dictionary and the main for loop
	running at most V times, all other operations are O(1) time.  Overall, the runtime is O(V)+O(V^2). 

Algorithms for question 2 for betweenness centrality algorithm 2: 

	Brandes algorithm’s runtime is O(V*N) where V is the
	vertices and N the edges. See reference sites for detail explanation. 

Algorithms for question 2 for exportTopDegreeGraphs algorithm 3:

	Total runtime is O(m*n*e) where M is the number of TOP 
	sorted values to display and n is for each vertex associated every TOP sorted values and e is each edge to add to the
	newly created graph for the return results. 

Algorithms for question 2 for returnTopCentralityFor algorithm 4: 

	Total runtime is O(m*n) where M is the number of TOP
	sorted values to display and n is for each vertex associated every TOP sorted values.  This algorithm calls a helper
	method to sort the values per type centrality which takes O(nlogn) time. 

Algorithms for question 2 for sortFriendsBy algorithm 5: 

	sorting helper method which takes O(nlogn) time

Testing:

I created some small datasets and tests implemented within the main function. I also created a separate Junit class for
testing. 

Tests include:

    • degree of centrality testing the sizes of various Top M requests are as expected.  
    • betweenness of centrality testing friendNode is the TOP importance. 
    • suggest friends of friends testing that expected unconnected friends are being recommended. 
    • test export graph methods and sizes of the input’s result set. 

Classes:

	•	FriendGraph - This structure will represent a classic graph using an adjacency list.  The graph is undirected and unweighted. This is an interface describing the implementation details of our graph. 
	•	FriendNode - This structure represents an individual as a node (vertex) within a graph. This node stores outgoing edges only as this is an undirected graph.  It holds all the centrality types. 
	•	FriendGraphImpl - This class implements all the methods and member variables to represent the graph interface. 
	•	FriendGraphTester - This class contains all the Junit test cases. 


