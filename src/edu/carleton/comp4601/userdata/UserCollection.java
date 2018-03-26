package edu.carleton.comp4601.userdata;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

import edu.carleton.comp4601.repository.MyMongoClient;

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
	
	//Utility Methods
	public void popluateCollection(){
		//Mongo Setup
		MyMongoClient mc = MyMongoClient.getInstance();
		DB database = mc.getDB();
		DBCollection users = database.getCollection("user");
		
		DBCursor curs = users.find();
		
		//Traverse collection
		try {
            while(curs.hasNext()) {
                DBObject o = curs.next();
                String name =       (String) o.get("name") ; 
                String friendsStr = (String) o.get("friends") ; 
                String ratingsStr = (String) o.get("ratings") ; 
                String genre =      (String) o.get("genre") ;
               
                //Convert FriendsStr to ArrayList<String>
                ArrayList<String> friends = new ArrayList<String>();
                friends.addAll(Arrays.asList(friendsStr.substring(1, friendsStr.length() - 1).split(", ")));
              
                //Convert ratingsStr to HashMap<String, Float>
                HashMap<String, Float> ratings = new HashMap<String, Float>();
                List<String> ratingsList = Arrays.asList(ratingsStr.substring(1, ratingsStr.length() - 1).split(", "));
                
                for(int i = 0; i< ratingsList.size(); i++){
                	String movie      = ratingsList.get(i).substring(0, ratingsList.get(i).lastIndexOf("="));
                	Float movieRating = Float.parseFloat(ratingsList.get(i).substring(ratingsList.get(i).lastIndexOf("=")+1));
                	ratings.put(movie, movieRating);
                }
                
                //Finally, add user to Collection
                User newUser = new User(name, ratings, friends, genre);
                System.out.println("Name :" + newUser.getName());
    			System.out.println("freinds :" + newUser.getFreinds().toString());
    			System.out.println("ratings :" + newUser.getRatings().toString());
    			System.out.println("genre :" + newUser.getBuffGenre());
    			
                this.addUser(newUser);
            }

        } catch (MongoException x) {
            x.printStackTrace();
        }
		
		

	}

}
