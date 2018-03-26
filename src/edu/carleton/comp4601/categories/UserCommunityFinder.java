package edu.carleton.comp4601.categories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.mongodb.util.Hash;

import Jama.Matrix;

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
	public HashSet<String> getFriendsOfAndIncluding(String user) {
		HashSet<String> friends = new HashSet<String>();
		friends.add(user);
		for(String friend: getFriendsOf(user))
			friends.add(friend);
		return friends;
	}
	
	public HashSet<String> getMoviesRatedBy(HashSet<String> friends) {
		HashSet<String> movies = new HashSet<String>();
		for(String friend: friends)
			for(String movie: getMoviesOf(friend))
				movies.add(movie);
		return movies;
	}
	
	public Matrix getMatrixOfUserMovieReviews(ArrayList<String> users, ArrayList<String> movies) {
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
		switch(movie){
		case "B00871C09S":
		case "B00007FCTH":
			return "I've had a lot of recent HD, Blu-ray version letdowns and this is certainly one of them, I won't review the movie because I assume any would be buyer is already a fan, but you should check this out since it does come with the old B&W classic original. Basically many of the scenes simply look BLURRY and out of focus, it's unavoidably noticeable and made me CRINGE in agony upon seeing such scenes in the movie. To me this is an overall letdown and I don't know if they can put out a better version... maybe it's just the nature of the source material that cannot be enhanced without also enhancing original problems with those certain camera shots?";
		case "B000F0V0LI":
		case "B00004CIQG":
			return "The 5 stars are for the film, 0 for this edition. Universal could easily have redone the sound on the last anniversary edition, but their marketing scheme was to wait a few years and then do it. I'm not being suckered into double dipping. My edition is just fine. What will the next edition have, a thread from Montana's suit? Spare me.";			
		case "630395345X":
		case "B00158K0S8":
			return "Since the invention of the dvd we've been waiting for this trilogy to be released, and now it finally has. But what can you say about it? It contains one of the classic heroes, one of the greatest films ever made, in fact one of the best trilogies we have and a bonus disk full of all kinds of great stuff. You have two of our best filmmakers coming together with one of our greatest actors. Classic stuff.";
		}
		return UserCommunityFinder.NO_REVIEW;
	}
	
	
	private HashSet<String> getMoviesOf(String friend) {
		return testMovieList.get(friend);
	}
	
	private HashSet<String> getFriendsOf(String user) {
		return testUserRelationships.get(user);
	}
	
	public static void main(String[] args) {
		UserCommunityFinder finder = new UserCommunityFinder();
		
		finder.getMatrixOfUserMovieReviews(
				new ArrayList<String>(finder.getFriendsOfAndIncluding("AJKWF4W7QD4NS")), 
				new ArrayList<String>(finder.getMoviesRatedBy(finder.getFriendsOfAndIncluding("AJKWF4W7QD4NS")))
		).print(2, 2);
		
		for(String movie: finder.getMoviesRatedBy(finder.getFriendsOfAndIncluding("AJKWF4W7QD4NS")))
			System.out.print(movie + " ");
	}
}
