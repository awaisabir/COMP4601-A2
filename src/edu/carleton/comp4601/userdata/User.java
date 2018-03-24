package edu.carleton.comp4601.userdata;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	
	private String name;
	private HashMap<String, Float> ratings;
	private ArrayList<User> friends; //would user UserCollection but it is singleton...
	private String buffGenre;
	
	//Constructors
	public User(String name){ 
		this.name = name; 
		buffGenre = "unfilled";
		ratings = new HashMap<String, Float>();
		friends = new ArrayList<User>();
	}
	public User(String name, HashMap<String, Float> ratings, ArrayList<User> friends){
		this.name = name;
		this.ratings = ratings;
		this.friends = friends;
	}
	
	//Setters
	public void setName(String name){this.name = name;}
	public void setRatings(HashMap<String, Float> ratings){ this.ratings = ratings; }
	public void setFriends(ArrayList<User> friends){ this.friends = friends; }
	public void setBuffGenre(String buffGenre){this.buffGenre = buffGenre;}
	
	//Getters
	public String getName(){ return name;}
	public HashMap<String, Float> getRatings(){ 		
		//Used For testing...
		if (ratings.size() == 0){ 
			ratings.put("default1", (float) 0.1);
			ratings.put("default2", (float) 0.2);
		}
		return ratings; 
	}
	public ArrayList<User> getFreinds(){ return friends; }
	public String getBuffGenre(){ return buffGenre; }
	

}
