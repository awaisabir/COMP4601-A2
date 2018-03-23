package edu.carleton.comp4601.categories;

import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Categorizer {
	String[] movieGenre = {"Action","Comedy","Horror"};	
	HashMap<String, Integer> wordCount;
	int counter = 0;
	public Categorizer() {
		String[] reviews = {"Here I Am!", "What are you doing", "Where do you go"};
		wordCount = new HashMap<String, Integer>();
		for(String review: reviews) {
			Matcher m = Pattern.compile("([\\w]*) ?").matcher(review);
			counter = 0;
			while(m.find() && counter++ < 100){
				if(!wordCount.containsKey(m.group(1)))
					wordCount.put(m.group(1), 0);
			}
		}
	}
	
	public static void main(String[] args){
		new Categorizer();
	}
	public void train() {
		
		
	}
	
	
	
	
	public HashMap<Integer, String> categorize() {
//		RandomForest rfClassifier = new RandomForest();
//		rfClassifier.buildClassifier(DataCleaner.getInstances());
//	
//		weka.filters.supervised.attribute.AttributeSelection as = new  weka.filters.supervised.attribute.AttributeSelection();
//	    Ranker ranker = new Ranker();
//
//
//
//	    InfoGainAttributeEval infoGainAttrEval = new InfoGainAttributeEval();
//	    as.setEvaluator(infoGainAttrEval);
//	    as.setSearch(ranker);
//	    as.setInputFormat(DataCleaner.getInstances());
//	    Instances trainData = Filter.useFilter(DataCleaner.getInstances(), as);
//	    Evaluation evaluation = new Evaluation(trainData);
//	    evaluation.crossValidateModel(rfClassifier, trainData, 10, new Random(1));

//        System.out.println(evaluation.toSummaryString("\nResults\n======\n", true));
//        System.out.println(evaluation.toClassDetailsString());
//        System.out.println("Results For Class -1- ");
//        System.out.println("Precision=  " + evaluation.precision(0));
//        System.out.println("Recall=  " + evaluation.recall(0));
//        System.out.println("F-measure=  " + evaluation.fMeasure(0));
//        System.out.println("Results For Class -2- ");
//        System.out.println("Precision=  " + evaluation.precision(1));
//        System.out.println("Recall=  " + evaluation.recall(1));
//        System.out.println("F-measure=  " + evaluation.fMeasure(1));
		return null;
	}
	

}
