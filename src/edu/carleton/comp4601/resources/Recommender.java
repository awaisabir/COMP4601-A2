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
import edu.carleton.comp4601.crawler.Controller;
import edu.carleton.comp4601.userdata.User;
import edu.carleton.comp4601.userdata.UserCollection;
//import edu.carleton.comp4601.resources.MyValues;

import org.json.*;
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
		name = "COMP4601 Recommender V1.0: Pierre Seguin and Awais Qureshi";
	
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
	@Produces(MediaType.APPLICATION_JSON)
	public String sayJSON() {
		return "{" + name + "}";
	}
	
	@GET
	@Path("/cat")
	@Produces(MediaType.APPLICATION_JSON)
	public String categ() {
		Categorizer c = new Categorizer();
		return "{" + "fuck" + "}";
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
			UserCollection.getInstance().popluateCollection();
			
		} 
		catch (Exception e) { e.printStackTrace(); }
		//---------------------------------------------------------------
		System.out.println("Popluate Collection Done");
		
		//Table setup
		html = html + "<table style= \"width:100%\"> <tr> <th>Name</th> <th>Movie Buff</th> <th>Ratings</th> <th>Friends</th> </tr>";
		
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
	
}
