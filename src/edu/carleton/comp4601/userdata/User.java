package edu.carleton.comp4601.userdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.xml.bind.annotation.XmlRootElement;

import edu.carleton.comp4601.resources.MyValues;

@XmlRootElement
public class User {
	
	private String name;
	private HashMap<String, Float> ratings;
	private ArrayList<String> friends; //would user UserCollection but it is singleton...
	private ArrayList<String> movies; //movies reviewed by a User
	private String buffGenre;
	
	//Constructors
	public User(String name){ 
		this.name = name; 
		
		//IMPORTANT NOTE: This is just used for testing!
		Random rand = new Random();
		int  n = rand.nextInt(3) + 0;
		buffGenre = MyValues.movieGenre[n];
		
		ratings = new HashMap<String, Float>();
		friends = new ArrayList<String>();
		movies  = new ArrayList<String>();
	}
	
	public User(String name, HashMap<String, Float> ratings, ArrayList<String> friends, ArrayList<String> movies, String genre){
		this.name = name;
		this.ratings = ratings;
		this.friends = friends;
		this.movies = movies;
		this.buffGenre = genre;
	}
	
	//Setters
	public void setName(String name) { this.name = name; }
	public void setRatings(HashMap<String, Float> ratings) { this.ratings = ratings; }
	public void setFriends(ArrayList<String> friends) { this.friends = friends; }
	public void setBuffGenre(String buffGenre) { this.buffGenre = buffGenre; }
	
	//Getters
	public String getName(){ return name;}
	public ArrayList<String> getFreinds(){ return friends; }
	public String getBuffGenre(){ return buffGenre; }
	public ArrayList<String> getMovies() { return this.movies; }
	public HashMap<String, Float> getRatings(){ 		
		//Used For testing...
		if (ratings.size() == 0){ 
			ratings.put("default1", (float) 0.1);
			ratings.put("default2", (float) 0.2);
		}
		return ratings; 
	}
	
	public boolean addMovieToList(String movieId) { return movies.add(movieId); }

	
}
