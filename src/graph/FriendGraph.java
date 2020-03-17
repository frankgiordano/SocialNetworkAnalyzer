package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface FriendGraph {

    public Map<Integer, FriendNode> getFriends();

    /* Creates a vertex with the given number id. */
    public void addVertex(int num);

    /* Creates an edge from the first vertex to the second. */
    public void addEdge(int from, int to);

    /*
     * Return the graph's connections in a readable format. The keys in this HashMap
     * are the vertices in the graph. The values are the nodes that are reachable
     * via a directed edge from the corresponding key. The returned representation
     * ignores edge weights and multi-edges.
     */
    public HashMap<Integer, HashSet<Integer>> exportGraph();

    /* find friends of friends not linked for friend recommendation */
    public HashMap<Integer, ArrayList<Integer>> suggestFriendsOfFriends(FriendNode person);

    /* Generate string representation of adjacency list */
    public String adjacencyString();

    /* export Top number of network by capacity */
    public List<FriendGraph> exportTopDegreeGraphs(int number);

    public void measureAndSetClosenessCentrality();

    public void measureAndSetBetweennessCentrality();

    public List<FriendNode> returnTopCentralityFor(int number, String string);

}
