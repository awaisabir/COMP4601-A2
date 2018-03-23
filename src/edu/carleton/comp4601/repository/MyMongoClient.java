package edu.carleton.comp4601.repository;

import java.net.UnknownHostException;
import java.util.HashMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;



public class MyMongoClient {
    MongoClient mc;
	DBCollection coll;
	
	HashMap<String,HashMap<String,DBCollection>> db;
	public MyMongoClient() {
		try {
			mc = new MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db = new HashMap<String,HashMap<String,DBCollection>>();
		for(String d: mc.getDatabaseNames()) {
			db.put(d, new HashMap<String, DBCollection>());
		}
	}
	public synchronized DBCollection getCollection(String dbName, String collectionName) {
		validateCollection(dbName,collectionName);
		return db.get(dbName).get(collectionName);
	}
	
	public synchronized boolean addToCollection(String dbName, String collectionName, BasicDBObject document){
		validateCollection(dbName,collectionName);
		if (findObject(dbName,collectionName,document) == null)
			db.get(dbName).get(collectionName).insert(document);
		else
			return false;
		return true;
	}
	
	public synchronized boolean addCollection(String dbName, String collectionName){
		mc.getDB(dbName).getCollection(collectionName);
		db.get(dbName).put(collectionName,(DBCollection)(mc.getDB(dbName).createCollection(collectionName, null)));
		return true;
	}
	
	public synchronized BasicDBObject findObject(String dbName, String collectionName, BasicDBObject document){
		validateCollection(dbName,collectionName);
		BasicDBObject obj = (BasicDBObject)db.get(dbName).get(collectionName).findOne(document);
		if(obj == null)
			return null;
		return obj;
	}
	
	public synchronized void updateInCollection (String dbName, String collectionName, BasicDBObject documentToFind, BasicDBObject documentWithUpdates) {
		validateCollection(dbName,collectionName);
		if(findObject(dbName,collectionName,documentToFind) == null) 
			db.get(dbName).get(collectionName).insert(documentWithUpdates);
		else {
			BasicDBObject doc2 = new BasicDBObject();
			doc2.put("$set", documentWithUpdates);
			db.get(dbName).get(collectionName).update(findObject(dbName,collectionName,documentToFind), doc2);

		}
	}
	public static void setInstance(MyMongoClient instance) {
		MyMongoClient.instance = instance;
	}
	
	private static MyMongoClient instance;
	public static MyMongoClient getInstance() {
		if (instance == null)
			instance = new MyMongoClient();
		return instance;
	}
	
	public void validateCollection(String dbName, String collectionName) {
			mc.getDB(dbName).getCollection(collectionName);
			if(!db.containsKey(dbName))
				db.put(dbName, new HashMap<String, DBCollection>());
			if (!db.get(dbName).containsKey(collectionName) && mc.getDB(dbName).getCollection(collectionName) != null)
				db.get(dbName).put(collectionName, (DBCollection) (mc.getDB(dbName)).getCollection(collectionName));			
	}
	
	public void dropCollection(String dbName, String collectionName) {
		mc.getDB(dbName).getCollection(collectionName).drop();
		db.get(dbName).remove(collectionName);
	}
	public void dropDB(String dbName) {
		mc.getDB(dbName).dropDatabase();
		db.remove(dbName);
	}	
}
