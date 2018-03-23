package edu.carleton.comp4601.categories;

import java.util.ArrayList;
import java.util.HashMap;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class RatingsClassifier {

	RandomForest rfClassifier;
	public RatingsClassifier(){
		rfClassifier = new RandomForest();
	}
	public void train(int docID, ArrayList<String> trainingData){
		Instances data = convertToInstances(trainingData);
		try {
			rfClassifier.buildClassifier(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer, Integer> getRatings(ArrayList<String> trainingData){
//		rfClassifier.classifyInstance(convertToInstance())
		return null;
	}
	public Instances convertToInstances(ArrayList<String> data){
		return null;
	}
}
