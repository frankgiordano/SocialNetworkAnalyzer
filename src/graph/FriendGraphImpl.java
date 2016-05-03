package graph;

/*
 * author: Frank Giordano 04/24/2016
 * This class will help to investigate friend relationships and influence within a social network. This class
 * represents a graph structure of friends. A friend is represented by an integer value. 
 * 
 * This class provides the methods needed to build a friendship graph with the following methods:
 * 
 * addVertex(int num)
 * addEdge(int from, int to)
 * 
 * The graph built is undirected and un-weighted. 
 * 
 * It provides further methods to investigate the built graph.  The following methods are:
 * 
 * suggestFriendsOfFriends(FriendNode person) - For a given person, which of their friends aren't connected 
 * as friends? Those that aren't connected we will suggest them as potential friends. 
 * 
 * The next set of methods will help to determine the persons with the most influence within a social network based
 * on centrality values of degree, closeness, and betweenness. For degree this is simply based on the number of 
 * connections a particular person\friend has.  This can determine popularity. For closeness, this is based on the distance
 * between all pairs of nodes\friends.  For betweenness, this is based on the number of times a node\friend appears
 * (or acts as a bridge) along the shortest path between two other nodes.  This can help to determine the person
 * which has the most communication flow between communities and is considered a better determination of influence than
 * the other centrality values noted. 
 * 
 * measureAndSetClosenessCentrality()
 * measureAndSetBetweennessCentrality()
 * 
 * For degree, this is simply tracked by the size of the friend node's edges\neighbors\friends. 
 * 
 * Another method to extract a list of TOP N friends in the graph based on centrality type is:
 * returnTopCentralityFor(int number, String type)
 * 
 * See main() method for examples on building a graph and using the noted methods above accordingly.  
 * 
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class FriendGraphImpl implements FriendGraph {
	
	private Map<Integer, FriendNode> friends;

	public void setFriends(Map<Integer, FriendNode> friends) {
		this.friends = friends;
	}

	private int numVertices;
	private int numEdges;
	
	public FriendGraphImpl() {
		this.friends = new HashMap<Integer, FriendNode>();
		this.numEdges = 0;
		this.numVertices = 0;
	}
	
	@Override
	public Map<Integer, FriendNode> getFriends() {
		return friends;
	}
	
	@Override
	public void addVertex(int num) {
		if (num < 0) throw new IllegalArgumentException("Number must be 0 or greater.");
		
		if (!friends.containsKey(num)) {
			FriendNode node = new FriendNode(num);
			friends.put(num, node);
			numVertices++;
		}
	}

	@Override
	public void addEdge(int from, int to) {
		if (!friends.containsKey(from)) {
			this.addVertex(from);
		}
		if (!friends.containsKey(to)) {
			this.addVertex(to);
		}
		
		FriendNode formNode = friends.get(from);
		FriendNode toNode = friends.get(to);
		formNode.addEdge(toNode);
		
		numEdges++;
	}

	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		HashMap<Integer, HashSet<Integer>> graph = new HashMap<Integer, HashSet<Integer>>();
		for (int v: friends.keySet()) {
			Set<FriendNode> nodes = friends.get(v).getEdges();
			HashSet<Integer> values = new HashSet<Integer>();
			for (FriendNode value: nodes) {
				values.add(value.getValue());
			}
			graph.put(v, values);
		}
		return graph;	
	}

	@Override
	public String adjacencyString() {
		String s = "Adjacency list";
		s += " (size " + numVertices + "+" + numEdges + " integers):";

		for (int v : friends.keySet()) {
			s += "\n\t"+v+": ";
			FriendNode node = friends.get(v);
			for (FriendNode w : node.getEdges()) {
				s += w.getValue()+", ";
			}
		}
		return s;
	}
	
	/**
	 * For a given network, return a list of subgraphs of a graph that includes only the most (N) influential
	 * based on the number of friend connections or as noted as degree centrality.  If N=1, this will return the 
	 * Top only most influential person(s).
	 * 
	 * @param number This is the number of top N influential subgraphs to return.
	 * @return List<FriendGraph> This is a list of graphs containing each individual influential person's graph.
	 */
	@Override
	public List<FriendGraph> exportTopDegreeGraphs(int number) {
		
		number--;
		if (number >= this.numVertices || number < 0) throw new IllegalArgumentException("Number must be less than num of vertices"); 
		
		List<FriendGraph> graphs = new LinkedList<FriendGraph>();
		HashMap<Double, ArrayList<FriendNode>> data = new HashMap<Double, ArrayList<FriendNode>>();
		
		ArrayList<Double> sortedValues = sortFriendsBy(data, "degree");
		
		int count = 0;	
		for (Double item: sortedValues) {
			
			if (count > number) break;
			ArrayList<FriendNode> vertices = data.get(item);
			for (FriendNode vertex: vertices) {
				FriendGraph graph = new FriendGraphImpl();
				Set<FriendNode> nodes = vertex.getEdges();
				for (FriendNode to: nodes) {
					graph.addEdge(vertex.getValue(), to.getValue());
				}
				graphs.add(graph);
			}
			count++;
		}
			
		return graphs;
	}
	
	/**
	 * For a given person, which of their friends aren't connected as friends? Those that
	 * aren't connected we will suggest them as potential friends. 
	 * 
	 * @param person This is the FriendNode that investigates its friends for recommendation(s)
	 * @return HashMap<Integer, ArrayList<Integer>> This returns a list of persons with a list 
	 * of friend recommendation(s) per person.
	 */
	@Override
	public HashMap<Integer, ArrayList<Integer>> suggestFriendsOfFriends(FriendNode person) {
		
		// create a list of all friends for searching recommendation(s)
		Set<FriendNode> friends = person.getEdges();
		// create a list of friends for friend recommendation(s)
		HashMap<Integer, ArrayList<Integer>> listOfRecommendations = new HashMap<Integer, ArrayList<Integer>>();
		
		for (FriendNode outerFriendNode: friends) {
			
			for (FriendNode innerFriendNode: friends) {
				
				if (outerFriendNode != innerFriendNode && !outerFriendNode.getEdges().contains(innerFriendNode)) {
					if (!listOfRecommendations.containsKey(outerFriendNode.getValue())) {
						ArrayList<Integer> list = new ArrayList<Integer>();
						list.add(innerFriendNode.getValue());
						listOfRecommendations.put(outerFriendNode.getValue(), list);
					} else {
						ArrayList<Integer> list = listOfRecommendations.get(outerFriendNode.getValue());
						list.add(innerFriendNode.getValue());
						listOfRecommendations.put(outerFriendNode.getValue(), list);
					}
				}
			}
		}
		
		return listOfRecommendations;
	}
	
	/**
	 * For a given network, return a list of friend nodes that includes only the most (N) influential
	 * based on centrality type.  If N=1, this will return the Top only most influential person(s).
	 * 
	 * @param number This is the number of top N influential nodes to return.
	 * @param type This is a string containing either "degree", "closeness" and "betweenness" centrality type
	 * @return List<FriendGraph> This is a list of friend nodes containing each individual influential TOP N persons.
	 */
	@Override
	public List<FriendNode> returnTopCentralityFor(int number, String type) {
		
		number--;  // start at 0
		if (number >= this.numVertices || number < 0) throw new IllegalArgumentException("Number must be less than num of vertices"); 
		
		ArrayList<FriendNode> friendsResult = new ArrayList<FriendNode>();
		HashMap<Double, ArrayList<FriendNode>> data = new HashMap<Double, ArrayList<FriendNode>>();
		
		ArrayList<Double> sortedValues = sortFriendsBy(data, type);
		
		int count = 0;	
		for (Double item: sortedValues) {
			
			if (count > number) break;
			ArrayList<FriendNode> vertices = data.get(item);
			for (FriendNode vertex: vertices) {
				friendsResult.add(vertex);
			}
			count++;
		}

		return friendsResult;
	}

	/**
	 * For a given network, measure the closeness centrality and save it within each friend node. This
	 * factors in distance between nodes\friends in a network. A distance is the number of links on 
	 * a path between two nodes which we denote here as a length. 
	 *  
	 * Measure for each friend node how far it is from the rest of the friend nodes. This is done by
	 * calling a helper method measureAllShortestPathLength() which retrieves all shortest path
	 * lengths. Their may be multiple shortest paths between two nodes which are not unique, but we 
	 * only note the length of the shortest path which is always unique. 
	 * 
	 * Setting the closeness centrality value is performed as a reverse division to provide
	 * a smaller average and higher centrality for closeness calculation (amount of paths/sum of all 
	 * lengths). 
	 */
	@Override
	public void measureAndSetClosenessCentrality() {
		
		// for each FriendNode, store the length of the shortest path to it from the startnode 
        HashMap<FriendNode, Integer> shortestPathLength = new HashMap<FriendNode, Integer>();
         
        // initialize the HashMap
        for (FriendNode n: this.friends.values()) {
            shortestPathLength.put(n, -1);
        }
		
		for (FriendNode friend: this.friends.values()) {
			
			shortestPathLength.put(friend, 0);
			measureAllShortestPathLength(friend, shortestPathLength);
			double allLength = 0;
			for (int value: shortestPathLength.values()){
				if (value != -1) {
					allLength += value;
				}
			}

			// reverse division to provide a smaller average and higher centrality
			// minus one from the size to not include the 0 value friendNode
			double closenessValue = (shortestPathLength.size()-1.0)/allLength;
			friend.setClosenessCentrality(closenessValue);
			
//			closenessVerboseOutput(shortestPathLength, friend, allLength, closenessValue);	
//			System.out.print("friend ID = "+ friend.getValue() + ", closeness =  ");
//			System.out.println(friend.getClosenessCentrality() + ", degree centrality = " + friend.getDegreeOfCentrality());
		}
		
	}

	@SuppressWarnings("rawtypes")
	private void closenessVerboseOutput(HashMap<FriendNode, Integer> shortestPathLength, FriendNode friend, double allLength, double value) {
		System.out.println("\nThe friend ID " + friend.getValue() + " being sent into measureAllShortestPathLength");
		System.out.println("shortestPathLength.size = " + (shortestPathLength.size()-1.0) + ", allLength = " + allLength);
		System.out.println("allLength/(shortestPathLength.size() = " + value);
		Iterator<Entry<FriendNode, Integer>> it = shortestPathLength.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry pair = it.next();
		    FriendNode key = (FriendNode) pair.getKey();
		    System.out.println(key.getValue() + " = " + pair.getValue());
		}
	}
	
	/**
	 * For a given friend node, find all shortest path lengths to each node in the network. This performs
	 * a BFS search. 
	 * 
	 * @param friendStartNode The friend node to start BFS search for all shortest paths to each node in network.
	 * @param shortestPathLength Stores the paths found and its lengths for each friend node. 
	 */
	private void measureAllShortestPathLength(FriendNode friendStartNode, HashMap<FriendNode, Integer> shortestPathLength) {
		
		HashSet<FriendNode> visited = new HashSet<FriendNode>();
		Queue<FriendNode> toExplore = new LinkedList<FriendNode>();
//		System.out.println("Start node = " + friendStartNode.getValue());
		// add start node to the queue and begin BFS type search
		toExplore.add(friendStartNode);
		while (!toExplore.isEmpty()) {
			// while the queue isn't empty remove the first node 
			FriendNode curr = toExplore.remove();
			int currDistance = shortestPathLength.get(curr);
			
			// otherwise look at all its unvisited neighbors and add to the 
			// visited set and enqueue
			for (FriendNode nieghbor: curr.getEdges()) {
				if (visited.contains(nieghbor)) {
					continue;
				}	
					
				toExplore.add(nieghbor);
				visited.add(nieghbor);
//				System.out.println("putting = " + nieghbor.getValue());
				shortestPathLength.put(nieghbor, currDistance+1);
			}
			visited.add(curr);
		}
//		System.out.println();
		
	}
	
	/**
	 * For a given network, measure the betweenness centrality and save it within each friend node.  
	 * Measure for each friend node how many shortest path(s) are their between a pair of friends. How
	 * many of these shortest path(s) contain the current friend node?  This is another way to measure
	 * importance of the friend within a network.  
	 * 
	 * Calculating the unweighed betweenness centrality using Brandes' Algorithm. Brandes takes a different
	 * approach compare to the brute force way of summing up all of the pair-wise dependencies. Brandes
	 * calculates betweenness based on a dependency accumulation technique which accesses the nodes in 
	 * reverse order of the BFS traversal. 
	 * 
	 * This method handles a unweighted graph and performs a basic BFS traversal as such. 
	 * 
	 * Followed the pseudocode from the following document http://algo.uni-konstanz.de/publications/b-fabc-01.pdf,
	 * "A Faster Algorithm for Betweenness Centrality" by Ulrik Brandes
	 * Another reference site http://www.cc.gatech.edu/~bader/papers/FastStreamingBC-SocialComputing2012.pdf 
	 */
	@Override
	public void measureAndSetBetweennessCentrality() {
		
		for (FriendNode friendStartNode: this.friends.values()) {
			
			Stack<FriendNode> stack = new Stack<FriendNode>();
			
			HashMap<FriendNode, LinkedList<FriendNode>> predecessors = new HashMap<FriendNode, LinkedList<FriendNode>>();
			HashMap<FriendNode, Integer> shortestPathCount = new HashMap<FriendNode, Integer>();
			HashMap<FriendNode, Integer> shortestPathDistance = new HashMap<FriendNode, Integer>();
			
			for (FriendNode friend: this.friends.values()) {
				shortestPathCount.put(friend, 0);
				shortestPathDistance.put(friend, -1);
				
				LinkedList<FriendNode> list = new LinkedList<FriendNode>();
				predecessors.put(friend, list);
			}
			
			shortestPathCount.put(friendStartNode, 1);
			shortestPathDistance.put(friendStartNode, 0);
			
			Queue<FriendNode> queue = new LinkedList<FriendNode>();
			queue.add(friendStartNode);
			
			// perform a bfs type search
			while (!queue.isEmpty()) {
				FriendNode currFriendNode = queue.remove();
				stack.push(currFriendNode);
				
				for (FriendNode neighbor: currFriendNode.getEdges()) {
					
					if (shortestPathDistance.get(neighbor) < 0) {
						queue.add(neighbor);
						shortestPathDistance.put(neighbor, shortestPathDistance.get(currFriendNode) + 1);
					}
					
					if (shortestPathDistance.get(neighbor) == (shortestPathDistance.get(currFriendNode) + 1)) {
						shortestPathCount.put(neighbor, shortestPathCount.get(neighbor) + shortestPathCount.get(currFriendNode));
						LinkedList<FriendNode> list = predecessors.get(neighbor);
						list.add(currFriendNode);
						predecessors.put(neighbor, list);
					}
				}
			}
	
			HashMap<FriendNode, Integer> dependency = new HashMap<FriendNode, Integer>();
			for (FriendNode friend: this.friends.values()) {
				dependency.put(friend, 0);
			}
		
			while (!stack.isEmpty()) {
				FriendNode currFriendNode = stack.pop();
				for (FriendNode friendNode: predecessors.get(currFriendNode)) {
					dependency.put(friendNode, dependency.get(friendNode) 
											   + (shortestPathCount.get(friendNode)/shortestPathCount.get(currFriendNode)
								               * (1 + dependency.get(currFriendNode))));
					if (currFriendNode != friendStartNode) {
						currFriendNode.setBetweennessValue(currFriendNode.getBetweennessValue() + dependency.get(currFriendNode));
					}
				}
			}
			
		}  // end main for loop
	}
	

	/**
	 * For a given network, take all of its vertices (friends) and retrieve the type value 
	 * for each vertex and store the type's value as a key within a hashmap variable called data.
	 * The value for the hashmap will contain the vertex ID (value) within a list. The hashmap data
	 * is used to create another list of key type values and sorts them in descending order. This 
	 * sorted list is return to the calling method. The calling method will also have a reference 
	 * to the data variable which is populated to be used further within the calling method. 
	 * 
	 * @param data A Hashmap that contains type values of the vertices and a list of vertices ID (values) for each type value.
	 * @return ArrayList<Double> This is a list of vertex size values sorted by descending order.
	 */
	private ArrayList<Double> sortFriendsBy(HashMap<Double, ArrayList<FriendNode>> data, String type) {
		
		// store key value pair within a hashmap to help handle duplicate type values
		// the key is a FriendNode number of edges and the value is a list of those FriendNodes 
		for (FriendNode friend: this.friends.values()) {
			double value = 0.0;
			switch (type) {
				case "degree": {
					value = friend.getDegreeOfCentrality();
					break;
				}
				case "closeness": {
					value = friend.getClosenessCentrality();
					break;
				}
				case "betweenness": {
					value = friend.getBetweennessValue();
					break;
				}
				default:
					throw new IllegalArgumentException("Invalid type");
			}
				
			if (!data.containsKey(value)) {
				ArrayList<FriendNode> vertices = new ArrayList<FriendNode>();
				vertices.add(friend);
				data.put(value, vertices);
			} else {
				ArrayList<FriendNode> vertices = data.get(value);
				vertices.add(friend);
				data.put(value, vertices);
			}
		}
		
		// get sorted order of values from data hashmap
		Set<Double> values = data.keySet();
		ArrayList<Double> sortedValues = new ArrayList<Double>(values);
		
		// sort values in descending order
		Collections.sort(sortedValues, new Comparator<Double>() {
		    @Override
		    public int compare(Double o1, Double o2) {
		    	 return o2.compareTo(o1);
		    }
		});
		
		return sortedValues;
	}
	
	/*
	 * Some test code.. See FriendGraphTester class for further testing done via JUnit
	 */
	public static void main(String[] args) {
		
		FriendGraph graph1 = new FriendGraphImpl();
		testData1(graph1);
		
		FriendGraph graph2 = new FriendGraphImpl();
		testData2(graph2);
	
		System.out.println(graph1.adjacencyString());
		System.out.println(graph1.exportGraph());
		System.out.println();
		List<FriendGraph> graphs1 = graph1.exportTopDegreeGraphs(1);
		for (FriendGraph g: graphs1) {
			System.out.println("TOP 1");
			System.out.println(g.adjacencyString());
		}
		
		List<FriendGraph> graphs2 = graph2.exportTopDegreeGraphs(3);
		for (FriendGraph g: graphs2) {
			System.out.println(g.adjacencyString());
		}
		
		System.out.println();
		System.out.println("friend suggestions = " + graph1.suggestFriendsOfFriends(graph1.getFriends().get(25)));
		System.out.println("friend suggestions = " + graph2.suggestFriendsOfFriends(graph2.getFriends().get(40)));
		System.out.println();
		
		graph2.measureAndSetClosenessCentrality();
		graph2.measureAndSetBetweennessCentrality();
		
		for (FriendNode friend: graph2.returnTopCentralityFor(2, "degree")) {
			System.out.println("Degree of centrality for person ID = " + friend.getValue() + ", centrality = " + friend.getDegreeOfCentrality());
		}
		
		for (FriendNode friend: graph2.returnTopCentralityFor(5, "closeness")) {
			System.out.println("Closeness of centrality for person ID = " + friend.getValue() + ", centrality = " + friend.getClosenessCentrality());
		}
		
		for (FriendNode friend: graph2.returnTopCentralityFor(2, "betweenness")) {
			System.out.println("Betweenness of centrality for person ID = " + friend.getValue() + ", centrality = " + friend.getBetweennessValue());
		}
		
	}
	
	private static void testData1(FriendGraph graph) {
		// Warm up algorithm 2: Strongly Connected Components - graph setup for testing
		graph.addVertex(32);
		graph.addEdge(32, 50);
		graph.addEdge(32, 44);
		graph.addVertex(50);
		graph.addVertex(44);
		graph.addEdge(44, 50);
		graph.addVertex(18);
		graph.addEdge(18, 23);
		graph.addEdge(18, 44);
		graph.addVertex(25);
		graph.addEdge(25, 23);
		graph.addEdge(25, 65);
		graph.addEdge(25, 18);
		graph.addVertex(65);
		graph.addEdge(65, 23);
		graph.addVertex(23);
		graph.addEdge(23, 18);
		graph.addEdge(23, 25);
		graph.addEdge(23, 65);
		graph.addEdge(50, 23);
	}

	private static void testData2(FriendGraph graph) {
		
		graph.addEdge(10, 20);
		graph.addEdge(20, 10);
		
		graph.addEdge(20, 30);
		graph.addEdge(30, 20);
		
		graph.addEdge(30, 40);
		graph.addEdge(40, 30);
		
		graph.addEdge(30, 50);
		graph.addEdge(50, 30);
		
		graph.addEdge(40, 50);
		graph.addEdge(50, 40);
		
		graph.addEdge(40, 60);
		graph.addEdge(60, 40);
		
		graph.addEdge(50, 60);
		graph.addEdge(60, 50);
	}

}
