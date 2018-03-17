package edu.carleton.comp4601.graph;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Vertex {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String url;
	int docID;

	public Vertex(String url, int docid){
		this.url = url;
		this.docID = docid;
		
	}
	
	public void setURL(String url) { this.url = url;	}
	public String getURL() { return url; }
	public void setDocID(int docid) { this.docID = docid; }
	public int getDocID() { return docID; }
}
