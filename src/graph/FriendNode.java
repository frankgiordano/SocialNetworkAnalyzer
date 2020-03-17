package graph;

import java.util.HashSet;
import java.util.Set;

public class FriendNode implements Comparable<FriendNode> {

    private int value;
    private Set<FriendNode> edges;
    private double closenessCentrality;
    private double betweennessValue;

    public FriendNode(int value) {
        if (value < 0)
            throw new IllegalArgumentException("Number must be 0 or greater.");
        this.value = value;
        this.edges = new HashSet<FriendNode>();
        this.setBetweennessValue(0);
    }

    public boolean addEdge(FriendNode node) {
        // add a friend
        if (!edges.contains(node)) {
            edges.add(node);
            return true;
        }

        return false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Set<FriendNode> getEdges() {
        return edges;
    }

    public void setEdges(Set<FriendNode> edges) {
        this.edges = edges;
    }

    public int getSize() {
        return this.edges.size();
    }

    public int getDegreeOfCentrality() {
        return this.edges.size();
    }

    public double getClosenessCentrality() {
        return closenessCentrality;
    }

    public void setClosenessCentrality(double closenessCentrality) {
        this.closenessCentrality = closenessCentrality;
    }

    @Override
    public int compareTo(FriendNode o) {
        if (this.getDegreeOfCentrality() < o.getDegreeOfCentrality())
            return 1;
        if (this.getDegreeOfCentrality() > o.getDegreeOfCentrality())
            return -1;
        return 0;
    }

    public double getBetweennessValue() {
        return betweennessValue;
    }

    public void setBetweennessValue(double betweennessValue) {
        this.betweennessValue = betweennessValue;
    }

}
