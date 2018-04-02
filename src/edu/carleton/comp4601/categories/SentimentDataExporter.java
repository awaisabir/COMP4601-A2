package edu.carleton.comp4601.categories;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import edu.carleton.comp4601.repository.MyMongoClient;
import edu.carleton.comp4601.userdata.User;
import edu.carleton.comp4601.userdata.UserCollection;

public class SentimentDataExporter {
	public void start(){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./sentiment.csv"));
			writer.write("User-Review,GOOD,BAD\n");
			int count = 0;

			MyMongoClient mc = MyMongoClient.getInstance();
			DB database = mc.getDB();
			DBCollection reviews = database.getCollection("reviews");
			DBCursor cursor = reviews.find();
			while( cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next(); 
				writer.write(obj.getString("user") + "-" + obj.getString("movie") + "," + UserRatingSentiment.getReviewSentimentCountAsString(obj.getString("review")) + "\n");
				count++;
				if(count%20 == 0)
					writer.flush();
			}
			writer.close();
		} catch(Exception e) {}
	}
	
	public static void main(String[] args){
		(new SentimentDataExporter()).start();
	}
}
