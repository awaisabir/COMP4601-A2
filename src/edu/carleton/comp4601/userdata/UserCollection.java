package edu.carleton.comp4601.userdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.mongodb.BasicDBObject;
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
		DBCollection users = database.getCollection("users");
		DBCollection reviews = database.getCollection("reviews");
		
		DBCursor curs = users.find();
		
		//Traverse collection
		try {
            while(curs.hasNext()) {
                DBObject o = curs.next();
                String name       = (String) o.get("name") ; 
                String friendsStr = (String) o.get("friends") ; 
                String ratingsStr = (String) o.get("ratings") ; 
                String genre      = (String) o.get("genre") ;
                
                DBObject query = new BasicDBObject();
                query.put("user", name);
                
                DBCursor moviesByUser = reviews.find(query);
                ArrayList<String> movies = new ArrayList<String>();
                
                while (moviesByUser.hasNext()) {
                	DBObject m = moviesByUser.next();
                	movies.add(m.get("movie").toString());
                }
               
                //Convert FriendsStr to ArrayList<String>
                ArrayList<String> friends = new ArrayList<String>();
                friends.addAll(Arrays.asList(friendsStr.substring(1, friendsStr.length() - 1).split(", ")));
              
                //Convert ratingsStr to HashMap<String, Float>
                HashMap<String, Float> ratings = new HashMap<String, Float>();
                List<String> ratingsList = Arrays.asList(ratingsStr.substring(1, ratingsStr.length() - 1).split(", "));
                
                for(int i=0; i<ratingsList.size(); i++){
                	String movie      = ratingsList.get(i).substring(0, ratingsList.get(i).lastIndexOf("="));
                	Float movieRating = Float.parseFloat(ratingsList.get(i).substring(ratingsList.get(i).lastIndexOf("=")+1));
                	ratings.put(movie, movieRating);
                }
                                
                // Finally, add user to Collection
                User newUser = new User(name, ratings, friends, movies, genre);
                this.addUser(newUser);
            }

        } catch (MongoException x) {
            x.printStackTrace();
        }
	}
}