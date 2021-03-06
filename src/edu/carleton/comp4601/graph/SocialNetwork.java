package edu.carleton.comp4601.graph;

import java.io.Serializable;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

public class SocialNetwork implements Serializable {
	DefaultDirectedGraph<Vertex, DefaultEdge> g;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SocialNetwork() {
		g = new DefaultDirectedGraph<Vertex, DefaultEdge>(DefaultEdge.class);
	}


	public DefaultDirectedGraph<Vertex,DefaultEdge> getGraph() { return g; }
	public Set<DefaultEdge> adjacentTo(Vertex v) { return g.edgesOf(v); }
	public int degree(Vertex v) { return g.edgesOf(v).size(); }
	public synchronized boolean hasVertex(Vertex v) { return g.containsVertex(v); }
	public synchronized boolean hasEdge(Vertex v, Vertex w) { return g.containsEdge(v,w); }
	public synchronized boolean addEdge(Vertex v, Vertex w) {
		g.addEdge(v, w);
		return true;
	}
	
	private static SocialNetwork instance;
	
	public static void setInstance(SocialNetwork object) {
		SocialNetwork.instance = object;
	}
	public static SocialNetwork getInstance() {
		if (instance == null)
			instance = new SocialNetwork();
		return instance;
	}
	
	//This code is made so we can have a separate graph for (just) Users
	private static SocialNetwork instance2;
	
	public static void setInstanceUser(SocialNetwork object) {
		SocialNetwork.instance2 = object;
	}
	
	public static SocialNetwork getInstanceUser() {
		if (instance2 == null)
			instance2 = new SocialNetwork();
		return instance2;
	}
	
    public synchronized Vertex getVertex(String url) {
   	 for(Vertex v: g.vertexSet()) {
   		if(v.getURL().equals(url))
   			return v;
   	 }
   	 
   	 return null;
    }
    
    public synchronized Vertex getVertex(int docid) {
	  	 for(Vertex v: g.vertexSet()) {
	  		if(v.getDocID() == docid)
	  			return v;
	  	 }
	  	 
	  	 return null;
   }
    
    public synchronized Boolean addVertex(Vertex v){ return g.addVertex(v); }
    
}
