package edu.carleton.comp4601.categories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import edu.carleton.comp4601.repository.MyMongoClient;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


public class Categorizer {
	//array containing the genres of movies we are looking for. ADD HERE for more genres to determine
	static String[] MOVIE_GENRE = {"Action","Comedy","Horror"};	
	HashMap<String, Integer> wordCount;
	int counter = 0;
	RandomForest rfClassifier = null;
	static Instances ins = null;
	
	MyMongoClient mc = MyMongoClient.getInstance();
	DB database = mc.getDB();
	DBCollection reviews = database.getCollection("reviews");
	DBCollection names = database.getCollection("movieNames");
	
	/* 
	 * Function:	Constructor
	 * Purpose:		Finds all unique words (first 100 words) for all documents
	 * 
	 */
	public Categorizer() {
		//get all review content from database
		ArrayList<String> reviewsArr = new ArrayList<String>(); //we could make this a normal array if we find the collection size of movieNames....		
		
		DBCursor cursName   = names.find();
		while(cursName.hasNext()) {
            DBObject o = cursName.next();
            String name = (String) o.get("movieName");
            
            BasicDBObject movie = new BasicDBObject();
            movie.put("movie", name);
            BasicDBObject r = mc.findObject("COMP4601-A2", "reviews", movie);
            String reviewText = r.getString("review");
            reviewsArr.add(reviewText);
		}
		
		
		wordCount = new HashMap<String, Integer>();
		for(String review: reviewsArr) {
			Matcher m = Pattern.compile("([\\w]*)\\s").matcher(review);
			counter = 0;
			while(m.find() && counter++ < 100){
				if(!wordCount.containsKey(m.group(1)))
					wordCount.put(m.group(1), 0);
			}
		}
		
		
		rfClassifier = new RandomForest();
		train();
	}

	public static void main(String[] args){
		Categorizer c = new Categorizer();
//		HashMap<String,Integer> words = c.getPageWords("ABC");
//		String keyList = "", valList = "";
//		for(String key: words.keySet()){
//			keyList += key + "\t";
//			valList += words.get(key) + "\t";
//		}
//		System.out.println(keyList);
//		System.out.println(valList);
	}
	
	
	
	/*
	 * Function: Train
	 * Parameters: none
	 * Purpose: takes the "Training" data, searches the database for the requested page
	 *  		and trains the classifier with the preset result 
	 */
	public void train() {
		//predetermined classification of movies made by Pierre Seguin **ADD MORE to improve accuracy**
		
		
		HashMap<String,Integer> trainingData = new HashMap<String,Integer>();
		trainingData.put("0784010331", 0);
		trainingData.put("0792158288", 0);
		trainingData.put("0792140923", 1);
		trainingData.put("0767800117", 0);
		trainingData.put("B000VDDWEC", 2);
		trainingData.put("B0000C24F3", 2);
		trainingData.put("B00004RJ74", 2);
		trainingData.put("B0083SJFZ2", 0);
		trainingData.put("B007XF4J70", 0);
		trainingData.put("B004RE29T0", 2);
		trainingData.put("B0000DK4QJ", 0);
		
		//POPULATE
		System.out.println("IN TRAIN");
		HashMap<String, Integer> dataToTrain = new HashMap<String,Integer>();
		DBCursor curs = names.find();
		while(curs.hasNext()) {
            DBObject o  = curs.next();
            String name = (String) o.get("movieName");
            
            dataToTrain.put(name, -1);
		}
	    buildClassifier(trainingData, dataToTrain);
	    System.out.println("size of dataToTrain: " + dataToTrain.size());
	}
	
	/*
	 * Function: 	getPageWords
	 * Parameters: 	String pageID
	 * Returns:		HashMap will ALL words and the count of their occurrences
	 * 
	 * Purpose:		Queries the database for the page content and stores all the counts of
	 * 				word occurrences to the HashMap 
	 */
	public HashMap<String,Integer> getPageWords(String pageID) {
		/*
		HashMap<String,Integer> pageCounts = new HashMap<String,Integer>(wordCount); 
		String page;
		//Retrieves the pageID Content 
		switch(pageID) {
			case "B004RE29T0":
				page = "I thought that the review of the following was great we are so great"; //get pageHTML
				break;
			default:
				page = "What are you doing";
				break;
		
		//searches all words and counts the occurrences
		for(String key: pageCounts.keySet())
			pageCounts.put(key, page.split("\\s?"+key+"\\s").length - 1);
		return pageCounts;
		
		*/
		//Step One: create hashmap
		HashMap<String,Integer> pageCounts = new HashMap<String,Integer>(wordCount); 
		
		//Step Two: set page to first 2 movie reviews
		BasicDBObject review = new BasicDBObject();
		review.put("movie", pageID);
		BasicDBObject result = mc.findObject("COMP4601-A2", "reviews", review);
		System.out.println("dude: " + result.get("user"));
		System.out.println("review yo: " +result.get("review"));
		
		String reviewStr = (String) result.get("review");
		
		//searches all words and counts the occurrences
		for(String key: pageCounts.keySet())
			pageCounts.put(key, reviewStr.split("\\s?"+key+"\\s").length - 1);
		
		System.out.println(pageCounts.toString());
		return pageCounts;
		
		
	}
	
	/*
	 * Function: 	buildClassifier
	 * Parameters: 	trainingData - HashMap<PageID,PredictionValues>, unknownValues
	 * 
	 * Purpose:		searches all the pages to train, and 'teaches' the remaining pages based
	 * 				on the training data
	 */
	public void buildClassifier(HashMap<String, Integer> trainingData, HashMap<String, Integer> unknownValues) {

		try {
			Instances training = buildTrainingData(trainingData);
			rfClassifier.buildClassifier(training);
			for(String key: unknownValues.keySet()){
				unknownValues.put(key,(int)testData(getPageWords(key)));
				System.out.println(key + ": " + unknownValues.get(key));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Function:	buildTrainingData
	 * Parameters:	trainingData - HashMap<PageID, PredictionValue>
	 * Returns:		Instances object containing all the training data
	 * 
	 * Purpose:		trains the classifier based on the word counts in the page and the
	 * 				preset results specified in train()
	 */
	Instances buildTrainingData(HashMap<String,Integer> trainingData){
		//gets data structure
		Instances newData = getInstancesOf(wordCount);
		
		int i = 0;
		//goes through the pages
		for(String key: trainingData.keySet()){
			//retrieves the dataset with the word counts of the pages specified
			HashMap<String,Integer> words = getPageWords(key);
			//creates a new Instance to train with
			newData.add(new DenseInstance(words.size()+1));
			//sets all the count of values
			for(String word: words.keySet())
				newData.get(i).setValue(newData.attribute(word),words.get(word).intValue());
			//sets the predetermined result
			newData.get(i).setValue(ins.attribute("TheMovieGenre"), MOVIE_GENRE[trainingData.get(key).intValue()]);
			i++;
		}
		return newData;
	}

	
	/*
	 * Function:	testData
	 * Parameters:	unknownValues - HashMap
	 * Returns:		the index of the genre determined from the data
	 * 
	 * Purpose:		Determines the classification of a SINGLE unknown page
	 * 				(HashMap contains the count of all the words in that page)
	 */
	int testData(HashMap<String, Integer> unknownValues){
		//creates instance to classify
		Instance newData = new DenseInstance(wordCount.size());
		//specifies the structure
		newData.setDataset(ins);
		//sets all values
		for(String key: unknownValues.keySet())
			newData.setValue(ins.attribute(key),unknownValues.get(key).intValue());
		//determines classification
		double pred = 0;
		try {
			pred = rfClassifier.classifyInstance(newData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (int)pred;
	}
	
	
	/*
	 * Function: 	getInstancesOf
	 * Parameters: 	wordCount - HashMap 
	 * 
	 * Purpose: 	Creates an instance object designed to specify the structure 
	 * 				of the classification data we use
	 */
	public static Instances getInstancesOf(HashMap<String, Integer> wordCount){
		if (ins != null)
			return ins;
		
		//adds the attributes associated to word counts
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for(String key: wordCount.keySet()) {
			attributes.add(new Attribute(key));
		}
		
		 // Declares the possible results of the classification (Genre)
		 ArrayList<String> fvNominalVal = new ArrayList<String>(Categorizer.MOVIE_GENRE.length);
		 for(String genre: Categorizer.MOVIE_GENRE)
			 fvNominalVal.add(genre);
		 Attribute attribute3 = new Attribute("TheMovieGenre", fvNominalVal);
		 attributes.add(attribute3);
		 //specifies the result class is the "TheMovieGenre" attribute
		 ins = new Instances("Rel",attributes, 10);
		 ins.setClass(ins.attribute("TheMovieGenre"));
		 return ins;
	}	

}
