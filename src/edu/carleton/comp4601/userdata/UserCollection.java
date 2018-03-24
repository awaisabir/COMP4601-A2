package edu.carleton.comp4601.userdata;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserCollection {
	
	private ArrayList<User> users;
	
	//Singelton Stuff
	public static void setInstance(UserCollection instance) {
		UserCollection.instance = instance;
	}
	public static UserCollection getInstance() {
		if (instance == null)
			instance = new UserCollection();
		return instance;
	}
	private static UserCollection instance;
	
	//Constructor
	public UserCollection(){
		users = new ArrayList<User>();
	}
	
	//Utility Methods
	public void addUser(User user){ users.add(user); }
	
	//Getters
	public ArrayList<User> getUsers(){ return users; }

}
