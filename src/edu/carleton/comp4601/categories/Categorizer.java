package edu.carleton.comp4601.categories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.attributeSelection.Ranker;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


public class Categorizer {
	String[] movieGenre = {"Action","Comedy","Horror"};	
	HashMap<String, Integer> wordCount;
	int counter = 0;
	RandomForest rfClassifier = null;
	Ranker ranker = null;

	Instances ins = null;
	
	public Categorizer() {
		String[] reviews = {"Here I Am!", "What are you doing", "Where do you go", "What did you think was going to happen", "Writing a review"};
		wordCount = new HashMap<String, Integer>();
		for(String review: reviews) {
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
		HashMap<String,Integer> words = c.getPageWords("ABC");
//		String keyList = "", valList = "";
//		for(String key: words.keySet()){
//			keyList += key + "\t";
//			valList += words.get(key) + "\t";
//		}
//		System.out.println(keyList);
//		System.out.println(valList);
	}
	public void train() {
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
		
		HashMap<String, Integer> dataToTrain = getPageWords("a");
		
		weka.filters.supervised.attribute.AttributeSelection as = new  weka.filters.supervised.attribute.AttributeSelection();
	    ranker = new Ranker();
	    
	    buildClassifier(trainingData, dataToTrain);

	}
	
	public HashMap<String,Integer> getPageWords(String pageID) {
		HashMap<String,Integer> pageCounts = new HashMap<String,Integer>(wordCount); 
		String page = "I thought that the review of the following was great we are so great"; //get pageHTML
		for(String key: pageCounts.keySet()){
			pageCounts.put(key, page.split("\\s?"+key+"\\s").length - 1);
		}
		return pageCounts;
	}
	
	public void buildClassifier(HashMap<String, Integer> trainingData, HashMap<String, Integer> unknownValues) {

		try {
			Instances training = buildTrainingData(trainingData);
			rfClassifier.buildClassifier(training);
			for(String key: unknownValues.keySet()){
				unknownValues.put(key,(int)testData(getPageWords(key)));
				System.out.println(unknownValues.get(key));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	Instances buildTrainingData(HashMap<String,Integer> trainingData){
		Instances newData = getInstancesOf(wordCount);
		
		int i = 0;
		for(String key: trainingData.keySet()){
			HashMap<String,Integer> words = getPageWords(key);
			newData.add(new DenseInstance(words.size()+1));
			for(String word: words.keySet())
				newData.get(i).setValue(newData.attribute(word),words.get(word).intValue());
			newData.get(i).setValue(ins.attribute("TheMovieGenre"),movieGenre[trainingData.get(key).intValue()]);
			i++;
		}
		return newData;
	}

	
	
	double testData(HashMap<String, Integer> unknownValues){
//		ins.attribute(key)
		
		Instance newData = new DenseInstance(wordCount.size());
		newData.setDataset(ins);
		int j = 0;
		for(String key: unknownValues.keySet()){
			newData.setValue(ins.attribute(key),unknownValues.get(key).intValue());
			j++;
		}
		double pred = 0;
		try {
			pred = rfClassifier.classifyInstance(newData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pred;
	}
	
	
	public Instances getInstancesOf(HashMap<String, Integer> wordCount){
		if (ins != null)
			return ins;

		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for(String key: wordCount.keySet()) {
			attributes.add(new Attribute(key));
		}
		
		 // Declare a nominal attribute along with its values
		 ArrayList<String> fvNominalVal = new ArrayList<String>(movieGenre.length);
		 for(String genre: movieGenre)
			 fvNominalVal.add(genre);
		 Attribute attribute3 = new Attribute("TheMovieGenre", fvNominalVal);
		 attributes.add(attribute3);

		 ins = new Instances("Rel",attributes, 10);
		 ins.setClass(ins.attribute("TheMovieGenre"));
		 return ins;
	}	

}
