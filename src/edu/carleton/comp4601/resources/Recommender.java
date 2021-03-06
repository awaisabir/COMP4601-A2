package edu.carleton.comp4601.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

//import javax.servlet.http.HttpServletResponse;
//import javax.websocket.OnClose;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//import org.apache.jasper.tagplugins.jstl.core.Set;
import org.apache.lucene.queryparser.classic.ParseException;


import Jama.Matrix;
import edu.carleton.comp4601.categories.Categorizer;
import edu.carleton.comp4601.categories.UserCommunityFinder;
import edu.carleton.comp4601.crawler.Controller;
import edu.carleton.comp4601.repository.MyMongoClient;
import edu.carleton.comp4601.userdata.User;
import edu.carleton.comp4601.userdata.UserCollection;
//import edu.carleton.comp4601.resources.MyValues;

import org.json.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
@Path("/")
public class Recommender {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	//Variable needed to notify /community when /context was run
	static boolean contextHit = false;

	private String name;

	// adding a test document
	public Recommender() throws IOException {
		name = "COMP4601 Recommender V1.0: Pierre Seguin, Awais Qureshi and Luke Daschko";
	
	}

	@GET
	public String printName() {
		return name;
	}

	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXML() {
		return "<?xml version=\"1.0\"?>" + "<bank> " + name + " </bank>";
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtml() {
		return "<html> " + "<title>" + name + "</title>" + "<body><h1>" + name
				+ "</body></h1>" + "</html> ";
	}
	
	@GET
	@Path("/advertising/{genre}")
	@Produces(MediaType.TEXT_HTML)
	public String advertising(@PathParam("genre") String genre){
		String html = "<html><head>\n<style>\n.images {text-align: center}\n img {max-height: 250px\n}\n</style></head><body><div class=\"images\">";
		
		for(BasicDBObject advert: MyMongoClient.getInstance().findObjects("COMP4601-A2", "Advertising", new BasicDBObject("genre", genre))){
			html = html + " " + advert.getString("value");
		}
		html = html + "</div> </body> </html>";
		
		return html;
	}
	
	@GET
	@Path("/context")
	@Produces(MediaType.TEXT_HTML)
	// Decr: displays all users (with info) in table format (Req. 8)
	public String context(){
		
		//html and table style setup
		String html = "<html> <body> <head><style>table, th, td {border: 1px solid black;}</style></head>";
		
		//NOT A FINAL SOLUTION-------------------------------------------
		try { 
			//ENSURE YOU HAVE CRAWLED
			UserCollection.getInstance();
			
		} 
		catch (Exception e) { e.printStackTrace(); }
		//---------------------------------------------------------------
		System.out.println("Popluate Collection Done");
		
		//Table setup
		html = html + "<table style= \"width:100%\"> <tr> <th>Name</th> <th>Movie Buff</th> <th>Ratings</th> <th>Friends</th> </tr>";
		
		UserCommunityFinder finder = new UserCommunityFinder();
		for(User u: UserCollection.getInstance().getUsers()) {
			u.setBuffGenre(finder.getPrediction(u.getName()));
			BasicDBObject obj = MyMongoClient.getInstance().findObject("COMP4601-A2", "users", new BasicDBObject("name", u.getName()));
			obj.put("genre", u.getBuffGenre());
			MyMongoClient.getInstance().updateInCollection("COMP4601-A2", "users", new BasicDBObject("name", u.getName()), obj);
			System.out.print(u.getName() + ": " + u.getBuffGenre());
		}
		
		//Add each user as row in table
		ArrayList<User> users = UserCollection.getInstance().getUsers();
		for(int i = 0; i < users.size(); i++){
			html = html + "<tr><td>"+ users.get(i).getName() + "</td><td>" + users.get(i).getBuffGenre() + "</td><td>" + users.get(i).getRatings().toString() + "</td><td>";
			//Add all friends (into one cell)
			for(int f = 0; f < users.get(i).getFreinds().size(); f++){
				html = html + users.get(i).getFreinds().get(f) + ", ";
			}
		}
		
		
		//closing html setup
		html = html + "</td>   </tr> </table></body></html>";	     
		
		contextHit = true;
		return html;
	}
	
	@GET
	@Path("/community")
	@Produces(MediaType.TEXT_HTML)
	public String community(){
		String html = "";
		
		//Uensure /context was visited first (Req. 9 states this is needed)
		if(contextHit != true){
			return "<html><body> <title> \"Error\" </title> <p>please run \\context first </p>  </body></html>";
		}
		else{
			//Inital html setup + table style setup
			html = "<html> <body> <head><style>table, th, td {border: 1px solid black;}</style></head>";
			
			//Table setup
			html = html + "<table style= \"width:100%\"> <tr> <th>Genre</th> <th>Users</th>  </tr>";
			
			//Create genre rows
			int numGenre = MyValues.movieGenre.length;
			HashMap<String, String> genreRows = new HashMap<String, String>();
			for(int g = 0; g < numGenre; g++){
				genreRows.put(MyValues.movieGenre[g], "<tr><td>" + MyValues.movieGenre[g] + "</td><td>"); 
			}
			
			
			//Populate genre rows with users
			ArrayList<User> users = UserCollection.getInstance().getUsers();
			for(int i = 0; i < users.size(); i++){
				genreRows.replace(users.get(i).getBuffGenre(), 
						          genreRows.get(users.get(i).getBuffGenre()) + users.get(i).getName() + ", ");
			}
			
			//Create final html string
			for(int h = 0; h < numGenre; h++){
				html = html + genreRows.get(MyValues.movieGenre[h]) + "</td> </tr>";
				
			}
			html = html + "</table> </body> </html>";
		}
		
		return html;
	}
	
	@GET
	@Path("/reset/{dir}")
	@Produces(MediaType.TEXT_HTML)
	public String reset(@PathParam("dir") String directory) {
		MyMongoClient.getInstance().dropCollection("COMP4601-A2", "graph");
		MyMongoClient.getInstance().dropCollection("COMP4601-A2", "movieNames");
		MyMongoClient.getInstance().dropCollection("COMP4601-A2", "reviews");
		MyMongoClient.getInstance().dropCollection("COMP4601-A2", "users");
		
		Thread crawlerThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String[] seeds = {
						"https://sikaman.dyndns.org/courses/4601/assignments/" + directory + "/pages",
						"https://sikaman.dyndns.org/courses/4601/assignments/" + directory + "/graph"
					};
					
					try {
						Controller c = new Controller(seeds);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			});
		
		crawlerThread.start();
		
		return "We out here ...... ";
	}
	
	@GET
	@Path("/{user}/{page}")
	@Produces(MediaType.TEXT_HTML)
	public String advertising(@PathParam("user") String user, @PathParam("page") String page){
		String html = "<html><head><style>.advertising {text-align: center;position:relative;float:left;max-width:15%;padding: 0 0 2em 0 }img {max-height: 250px;}.review {position:relative;float:left;max-width:80%;}</style></head><body><div class=\"review\">";
		BasicDBObject u = MyMongoClient.getInstance().findObject("COMP4601-A2", "users", new BasicDBObject("name", user));
		BasicDBObject m = MyMongoClient.getInstance().findObject("COMP4601-A2", "movieNames", new BasicDBObject("movieName", page));
		BasicDBObject review = MyMongoClient.getInstance().findObject("COMP4601-A2", "reviews", new BasicDBObject("user", user).append("movie", page));
		if(review != null)
			html = html + "<h2>" + user + "'s Review of " + page + "</h2><p>" + review.getString("review") + "</p>";
		else
			html = html + "<p> The user did not review this movie </p>";
		html = html + "</div> <div class=\"advertising\"><h3>Movie you might like:</h3>";
		if(u != null) {
			ArrayList<BasicDBObject> genreAds = MyMongoClient.getInstance().findObjects("COMP4601-A2", "Advertising", new BasicDBObject("genre", u.getString("genre")));
			html = html + genreAds.get(((int)(Math.random()*genreAds.size()))).getString("value");
		}
		html = html + "<br><h3>Based on your recent search:</h3>";
		if(m != null) {
			
			ArrayList<BasicDBObject> genreAds = MyMongoClient.getInstance().findObjects("COMP4601-A2", "Advertising", new BasicDBObject("genre", Categorizer.MOVIE_GENRE[m.getInt("genre")]));
			html = html + genreAds.get(((int)(Math.random()*genreAds.size()))).getString("value");
		}
		html = html + "</div> </body> </html>";
		
		return html;
	}
}
