package graph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FriendGraphTester {

	private FriendGraph graph1;
	private FriendGraph graph2;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception 
	{
		
	    graph1 = new FriendGraphImpl();
	    graph2 = new FriendGraphImpl();
		
		graph1.addEdge(10, 20);
		graph1.addEdge(20, 10);
		
		graph1.addEdge(20, 30);
		graph1.addEdge(30, 20);
		
		graph1.addEdge(30, 40);
		graph1.addEdge(40, 30);
		
		graph1.addEdge(30, 50);
		graph1.addEdge(50, 30);
		
		graph1.addEdge(40, 50);
		graph1.addEdge(50, 40);
		
		graph1.addEdge(40, 60);
		graph1.addEdge(60, 40);
		
		graph1.addEdge(60, 50);
		graph1.addEdge(50, 60);

		graph2.addVertex(32);
		graph2.addEdge(32, 50);
		graph2.addEdge(32, 44);
		graph2.addVertex(50);
		graph2.addVertex(44);
		graph2.addEdge(44, 50);
		graph2.addVertex(18);
		graph2.addEdge(18, 23);
		graph2.addEdge(18, 44);
		graph2.addVertex(25);
		graph2.addEdge(25, 23);
		graph2.addEdge(25, 65);
		graph2.addEdge(25, 18);
		graph2.addVertex(65);
		graph2.addEdge(65, 23);
		graph2.addVertex(23);
		graph2.addEdge(23, 18);
		graph2.addEdge(23, 25);
		graph2.addEdge(23, 65);
		graph2.addEdge(50, 23);
		
	}

	/** Test if the size method is working correctly.
	 */
	@Test
	public void testGraphAndBasicExport()
	{
		System.out.println("testGraphAndBasicExport = " + graph2.adjacencyString());
		graph2.measureAndSetClosenessCentrality();
		
		System.out.println("exportGraph = " + graph2.exportGraph());
		System.out.println();
		List<FriendGraph> topGraphFor1 = graph2.exportTopDegreeGraphs(1);
		for (FriendGraph g: topGraphFor1) {
			System.out.println("TOP 1");
			System.out.println(g.adjacencyString()+"\n");
		}
		
		List<FriendGraph> topGraphFor3 = graph2.exportTopDegreeGraphs(3);
		for (FriendGraph g: topGraphFor3) {
			System.out.println("TOP 3");
			System.out.println(g.adjacencyString()+"\n");
		}
		
		assertEquals(2, topGraphFor1.size());
		assertEquals(7, topGraphFor3.size());
		
		for (FriendNode friend: graph2.returnTopCentralityFor(2, "degree")) {
			System.out.println("Degrees of centrality for person ID = " + friend.getValue() + ", degree centrality = " 
								+ friend.getDegreeOfCentrality() + ", size = " + friend.getSize() + ", closeness centrality = " + friend.getClosenessCentrality());
		}
		
	}
	
	@Test
	public void testDegree()
	{
		
		System.out.println(graph1.adjacencyString());
		graph1.measureAndSetClosenessCentrality();
		
		for (FriendNode friend: graph1.returnTopCentralityFor(2, "degree")) {
			System.out.println("Degrees of centrality for person ID = " + friend.getValue() + ", degree centrality = " 
								+ friend.getDegreeOfCentrality() + ", size = " + friend.getSize() + ", closeness centrality = " + friend.getClosenessCentrality());
		}
	
		List<FriendNode> friendList = graph1.returnTopCentralityFor(2, "degree");
		assertEquals(friendList.get(0).getValue(), 50);
		assertEquals(friendList.get(1).getValue(), 40);
		assertEquals(friendList.get(2).getValue(), 30);
		assertEquals(friendList.get(3).getValue(), 20);
		assertEquals(friendList.get(4).getValue(), 60);
		
		assertEquals(friendList.get(0).getDegreeOfCentrality(), 3);
		assertEquals(friendList.get(1).getDegreeOfCentrality(), 3);
		assertEquals(friendList.get(2).getDegreeOfCentrality(), 3);
		assertEquals(friendList.get(3).getDegreeOfCentrality(), 2);
		assertEquals(friendList.get(4).getDegreeOfCentrality(), 2);
		
	}
	
	@Test
	public void testCloseness()
	{
		graph1.measureAndSetClosenessCentrality();
		List<FriendNode> friendList = graph1.returnTopCentralityFor(2, "closeness");
		
		double valueD = friendList.get(0).getClosenessCentrality();
		String valueS = Double.toString(valueD);

		assertEquals(valueS, "0.7142857142857143");
		
		double valueD1 = friendList.get(1).getClosenessCentrality();
		String valueS1 = Double.toString(valueD1);
		
		assertEquals(valueS1, "0.625");
		
	}
	
	@Test
	public void testBetweenness()
	{
		System.out.println(graph1.adjacencyString());
		graph1.measureAndSetBetweennessCentrality();
		List<FriendNode> friendList = graph1.returnTopCentralityFor(2, "betweenness");
		
		// check and make sure 30 id node is the most important with value 12.0
		int value = (int) friendList.get(0).getBetweennessValue();
		assertEquals(value, 12);
		
	}
	
	@Test
	public void testSuggestFriendsOfFriends() {
	
		System.out.println();
		System.out.println(graph2.suggestFriendsOfFriends(graph2.getFriends().get(25)));
		System.out.println();
		
		HashMap<Integer, ArrayList<Integer>> suggestions = graph2.suggestFriendsOfFriends(graph2.getFriends().get(25));
		
		ArrayList<Integer> suggestionsValuesFor65 = suggestions.get(65);
		int valueFor65 = suggestionsValuesFor65.get(0);
		assertEquals(valueFor65, 18);
		
		ArrayList<Integer> suggestionsValuesFor18 = suggestions.get(18);
		int valueFor18 = suggestionsValuesFor18.get(0);
		assertEquals(valueFor18, 65);
		
	}
	
}

