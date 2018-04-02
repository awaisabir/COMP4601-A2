package edu.carleton.comp4601.categories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.mongodb.BasicDBObject;

import Jama.Matrix;
import edu.carleton.comp4601.graph.CrawlGraph;
import edu.carleton.comp4601.graph.SocialNetwork;
import edu.carleton.comp4601.repository.Marshaller;
import edu.carleton.comp4601.repository.MyMongoClient;
import edu.carleton.comp4601.userdata.User;
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
		HashSet<String> friends = getFriendsOfAndIncluding(user);
		ArrayList<String> movies = new ArrayList<String>(getMoviesRatedBy(friends));
		Matrix m = getMatrixOfUserMovieReviews(new ArrayList<String>(friends),movies);
		HashMap<String, Integer> genres = new HashMap<String, Integer>(Categorizer.MOVIE_GENRE.length);
		ArrayList<String> genreNames = new ArrayList<String>(Arrays.asList(Categorizer.MOVIE_GENRE));
		for(int j = 0; j<Categorizer.MOVIE_GENRE.length; j++)
			genres.put(Categorizer.MOVIE_GENRE[j], 0);
		for(int i = 0; i< m.getColumnDimension(); i++)
			if(m.get(0, i) == 1d) {
				int gkey = MyMongoClient.getInstance().findObject("COMP4601-A2", "movieNames", new BasicDBObject("movieName",movies.get(i))).getInt("genre");
				genres.put(genreNames.get(gkey), genres.get(genreNames.get(gkey))+1);
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
			movies.addAll(getMoviesOf(friend));
		return movies;
	}
	
	private Matrix getMatrixOfUserMovieReviews(ArrayList<String> users, ArrayList<String> movies) {
		Matrix userMovieReviews = new Matrix(users.size(), movies.size(),-1d);
		for(String user: users)
			for(String movie: getMoviesOf(user)){
					userMovieReviews.set(users.indexOf(user), movies.indexOf(movie), UserCollection.getInstance().getUser(user).getRatings().get(movie));//UserRatingSentiment.getReviewSentiment(content));
			}
		return replaceAllMissingReviews(userMovieReviews);
	}
	
	
	private Matrix replaceAllMissingReviews(Matrix m){
		Matrix initialM = m.copy();
//		m.print(1,1);
//		for(int i = 0; i<m.getRowDimension(); i++)
		int i = 0;
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
		return new HashSet<>(UserCollection.getInstance().getUser(friend).getRatings().keySet());
	}
	
	private HashSet<String> getFriendsOf(String user) {
		return new HashSet<>(UserCollection.getInstance().getUser(user).getFreinds());
	}
	
	public static void main(String[] args) {
		try {
			CrawlGraph.setInstance((CrawlGraph)Marshaller.deserializeObject((byte[])MyMongoClient.getInstance().findObject("COMP4601-A2", "graph", new BasicDBObject()).get("reviewsGraph")));
			SocialNetwork.setInstance((SocialNetwork)Marshaller.deserializeObject((byte[])MyMongoClient.getInstance().findObject("COMP4601-A2", "graph", new BasicDBObject()).get("socialNetwork")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		UserCommunityFinder finder = new UserCommunityFinder();
		for(User u: UserCollection.getInstance().getUsers()) {
			u.setBuffGenre(finder.getPrediction(u.getName()));
			BasicDBObject obj = MyMongoClient.getInstance().findObject("COMP4601-A2", "users", new BasicDBObject("name", u.getName()));
			obj.put("genre", u.getBuffGenre());
			MyMongoClient.getInstance().updateInCollection("COMP4601-A2", "users", new BasicDBObject("name", u.getName()), obj);
			System.out.print(u.getName() + ": " + u.getBuffGenre());
		}
	}
}
