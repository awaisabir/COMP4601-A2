package edu.carleton.comp4601.categories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.Hash;

import Jama.Matrix;
import edu.carleton.comp4601.graph.CrawlGraph;
import edu.carleton.comp4601.graph.SocialNetwork;
import edu.carleton.comp4601.repository.Marshaller;
import edu.carleton.comp4601.repository.MyMongoClient;
import edu.carleton.comp4601.userdata.UserCollection;

public class UserCommunityFinder {
	HashSet<String> testFriendsList = new HashSet<String>(Arrays.asList("A2S166WSCFIFP5","A109LWN9DUGPDP","AJKWF4W7QD4NS"));
	HashMap<String,HashSet<String>> testMovieList;
	HashMap<String,HashSet<String>> testUserRelationships;
	static String NO_REVIEW = "REVIEW_NONE";
	
	public UserCommunityFinder(){
		testMovieList = new HashMap<String, HashSet<String>>();
		testUserRelationships = new HashMap<String, HashSet<String>>();

		testUserRelationships.put("A2S166WSCFIFP5",new HashSet<String>(Arrays.asList("A109LWN9DUGPDP","AJKWF4W7QD4NS")));
		testUserRelationships.put("A109LWN9DUGPDP",new HashSet<String>(Arrays.asList("AJKWF4W7QD4NS","A2S166WSCFIFP5")));
		testUserRelationships.put("AJKWF4W7QD4NS",new HashSet<String>(Arrays.asList("A109LWN9DUGPDP", "A2S166WSCFIFP5")));
		
		testMovieList.put("A2S166WSCFIFP5",new HashSet<String>(Arrays.asList("B00871C09S", "B00004CIQG", "B00158K0S8", "B00007FCTH", "0792158288", "B000F0V0LI", "B000F0V0LI")));
		testMovieList.put("A109LWN9DUGPDP",new HashSet<String>(Arrays.asList("B007XF4J66", "630395345X", "B00871C09S", "B00004CIQG", "B000F0V0LI", "B000F0V0LI")));
		testMovieList.put("AJKWF4W7QD4NS",new HashSet<String>(Arrays.asList("B007XF4J66", "B00004CIQG", "B00158K0S8", "B00007FCTH", "B000F0V0LI")));
	}
	
	public String getPrediction(String user) {
		HashSet<String> friends = getFriendsOfAndIncluding("AJKWF4W7QD4NS");
		ArrayList<String> movies = new ArrayList<String>(getMoviesRatedBy(friends));
		Matrix m = getMatrixOfUserMovieReviews(new ArrayList<String>(friends),movies);
		HashMap<String, Integer> genres = new HashMap<String, Integer>(Categorizer.MOVIE_GENRE.length);
		for(int j = 0; j<genres.size(); j++)
			genres.put(Categorizer.MOVIE_GENRE[j], 0);
		for(int i = 0; i< m.getColumnDimension(); i++)
			if(m.get(0, i) == 1d){
				String gkey = MyMongoClient.getInstance().findObject("COMP4601-A2", "reviews", new BasicDBObject("movie",movies.get(i))).getString("genre");
				genres.put(gkey, genres.get(gkey)+1);
			}
		
		String maxgenre = Categorizer.MOVIE_GENRE[0];
		for(String key: genres.keySet())
			if(genres.get(key) > genres.get(maxgenre))
				maxgenre = key;
		
		return maxgenre;
		
		
	}
	
	private HashSet<String> getFriendsOfAndIncluding(String user) {
		HashSet<String> friends = new HashSet<String>();
		friends.add(user);
		for(String friend: getFriendsOf(user))
			friends.add(friend);
		return friends;
	}
	
	private HashSet<String> getMoviesRatedBy(HashSet<String> friends) {
		HashSet<String> movies = new HashSet<String>();
		for(String friend: friends)
			for(String movie: getMoviesOf(friend))
				movies.add(movie);
		return movies;
	}
	
	private Matrix getMatrixOfUserMovieReviews(ArrayList<String> users, ArrayList<String> movies) {
		Matrix userMovieReviews = new Matrix(users.size(), movies.size(),-1d);
		for(String user: users)
			for(String movie: getMoviesOf(user))
				if(getReviewContent(user, movie) != UserCommunityFinder.NO_REVIEW)
					userMovieReviews.set(users.indexOf(user), movies.indexOf(movie), UserRatingSentiment.getReviewSentiment(getReviewContent(user, movie)));
		return replaceAllMissingReviews(userMovieReviews);
	}
	
	
	private Matrix replaceAllMissingReviews(Matrix m){
		Matrix initialM = m.copy();
		m.print(1,1);
		for(int i = 0; i<m.getRowDimension(); i++)
			for(int j = 0; j< m.getColumnDimension(); j++)
				if(initialM.get(i, j) == -1d)
					m.set(i,j,pred(i, j, initialM));
		return m;
	}
	
	
	public double sim(int a, int b, Matrix ratings) {
		double sim = 0d;
		double num = 0d;//sum of items((rating(item,a) - avgrating(a)) * (rating(item,b) - avgrating(b))
		double den1 = 0d;//sqrt(sum of items(sqr(rating(item, a) - avgrating(a)))
		double den2 = 0d;////sqrt(sum of items(sqr(rating(item, b) - avgrating(b)))
		double avgA = 0d;
		double avgB = 0d;
		ArrayList<Integer> rs = new ArrayList<Integer>();
		
		for(int i = 0; i< ratings.getRowDimension(); i++) {
			if(ratings.get(a,i) != -1 && ratings.get(b,i) != -1) {
				rs.add(i);
				avgA += ratings.get(a,i);
				avgB += ratings.get(b,i);
			}
		}
		avgA /= rs.size();
		avgB /= rs.size();
		for(int i: rs) {
			num += (ratings.get(a,i) - avgA)*(ratings.get(b,i) - avgB);
			den1 += Math.pow(ratings.get(a,i) - avgA, 2);
			den2 += Math.pow(ratings.get(b,i) - avgB, 2);
		}
		den2 = Math.sqrt(den2);
		den1 = Math.sqrt(den1);
		
		sim =  num / (den1 * den2);
		return sim;
	}
	
	public double pred(int a, int p, Matrix ratings){
		double avgA = 0d;
		double pred = 0d;
		double num = 0d;
		double den = 0d;
		int count = 0;
		for(int j = 0; j<ratings.getColumnDimension(); j++) {
			if(ratings.get(a, j) != -1) {
				avgA += ratings.get(a, j);
				count ++;
			}
		}
		avgA /= count;
		double avgB = 0d;
		for(int b = 0; b<ratings.getRowDimension(); b++)
			if(sim(a,b, ratings) >= 0) {	
				if(b != a) {
					count = 0;
					avgB = 0d;
					for(int j = 0; j<ratings.getColumnDimension(); j++) {
						avgB += ratings.get(b,j);
						count ++;
					}
					avgB /= count;
					
					num += sim(a, b, ratings)*(ratings.get(b,p) - avgB);
					den += sim(a, b, ratings);
				}
			}
		den = (den==0d)?1:den;
		pred = avgA + num/den;
		return Math.round(pred);
	}
	
	
	
	
	private String getReviewContent(String user, String movie) {
		if(MyMongoClient.getInstance().findObject("COMP4601-A2", "reviews", new BasicDBObject("user",user).append("movie", movie)) == null)
			return UserCommunityFinder.NO_REVIEW;
		return MyMongoClient.getInstance().findObject("COMP4601-A2", "reviews", new BasicDBObject("user",user).append("movie", movie)).getString("review");
		
	}
	
	
	private HashSet<String> getMoviesOf(String friend) {
		HashSet<String> movies = new HashSet<String>();
		BasicDBObject obj = MyMongoClient.getInstance().findObject("COMP4601-A2", "users", new BasicDBObject("name", friend));
		if (obj != null) {
			JSONArray arr = new JSONArray(obj.getString("movies"));
			for(int i = 0; i < arr.length(); i++)
				movies.add(arr.getString(i));
		}
//		for(DefaultEdge e: CrawlGraph.getInstance().getGraph().outgoingEdgesOf(CrawlGraph.getInstance().getVertex("https://sikaman.dyndns.org/courses/4601/assignments/training/users/"+friend+".html"))) {
//			int id = CrawlGraph.getInstance().getGraph().getEdgeTarget(e).getDocID();
//			movies.add(MyMongoClient.getInstance().findObject("COMP4601-A2", "reviews", new BasicDBObject("docId",id)).getString("movie"));
//		}
		return movies;
	}
	
	private HashSet<String> getFriendsOf(String user) {
		HashSet<String> friends = new HashSet<String>();
		BasicDBObject obj = MyMongoClient.getInstance().findObject("COMP4601-A2", "users", new BasicDBObject("name",user));
		if(obj != null){
			JSONArray arr = new JSONArray(obj.getString("friends"));
			for(int i = 0; i<arr.length(); i++)
				friends.add(arr.getString(i));
		}
		return friends;
	}
	
	public static void main(String[] args) {
		try {
			CrawlGraph.setInstance((CrawlGraph)Marshaller.deserializeObject((byte[])MyMongoClient.getInstance().findObject("COMP4601-A2", "graph", new BasicDBObject()).get("reviewsGraph")));
			SocialNetwork.setInstance((SocialNetwork)Marshaller.deserializeObject((byte[])MyMongoClient.getInstance().findObject("COMP4601-A2", "graph", new BasicDBObject()).get("socialNetwork")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(SocialNetwork.getInstance().getGraph().vertexSet());
		UserCommunityFinder finder = new UserCommunityFinder();
		System.out.println(finder.getFriendsOfAndIncluding("ABSX5TGEGRH76"));
		System.out.println(finder.getMoviesOf("ABSX5TGEGRH76"));
		HashMap<String,Integer> genres = new HashMap<String,Integer>();
		ArrayList genreNames = new ArrayList<>(Arrays.asList(Categorizer.MOVIE_GENRE));
		for(String genre: Categorizer.MOVIE_GENRE)
			genres.put(genre, 0);
		for(BasicDBObject obj: MyMongoClient.getInstance().findObjects("COMP4601-A2", "users", new BasicDBObject())) {
			HashSet<String> f = finder.getFriendsOfAndIncluding(obj.getString("name"));
			ArrayList<String> mov = new ArrayList<String>(finder.getMoviesRatedBy(f));
			Matrix m = finder.getMatrixOfUserMovieReviews(new ArrayList<String>(f), mov);

		}
		
//		System.out.println(finder.getMoviesOf("ABSX5TGEGRH76"));
//		finder.getMatrixOfUserMovieReviews(
//				new ArrayList<String>(finder.getFriendsOfAndIncluding("AJKWF4W7QD4NS")), 
//				new ArrayList<String>(finder.getMoviesRatedBy(finder.getFriendsOfAndIncluding("AJKWF4W7QD4NS")))
//		).print(2, 2);
//		
//		for(String movie: finder.getMoviesRatedBy(finder.getFriendsOfAndIncluding("AJKWF4W7QD4NS")))
//			System.out.print(movie + " ");
	}
}
