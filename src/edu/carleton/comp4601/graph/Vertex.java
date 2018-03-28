package edu.carleton.comp4601.graph;

import javax.xml.bind.annotation.XmlRootElement;

import com.mongodb.BasicDBObject;

@XmlRootElement
public class Vertex extends BasicDBObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String url;
	int docID;

	public Vertex(String url, int docid){
		this.url = url;
		this.docID = docid;
		put("url", url);
		put("docID", docid);
	}
	
	public void setURL(String url) { this.url = url;	}
	public String getURL() { return url; }
	public void setDocID(int docid) { this.docID = docid; }
	public int getDocID() { return docID; }
}
