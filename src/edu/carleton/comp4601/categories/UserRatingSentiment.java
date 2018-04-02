package edu.carleton.comp4601.categories;

import java.util.HashMap;

public class UserRatingSentiment {
	static int GOOD_SENTIMENT = 0;
	static int BAD_SENTIMENT = 1;
	static String[] SENTIMENT ={"GOOD","BAD"};
	static String[] goodSentimentFields = {"great", "fantastic", "5", "five", "4", "four", "good", "excellent", "like", "enjoy","laugh", "great", "loved", "brought", "wonderful", "fantasy", "genius"};
	static String[] badSentimentFields = {"zero", "meh", "0", "1", "not", "bad", "hate", "terrible", "slow", "suck", "horrible", "disliked", "don't", "inconsiderate", "understand", "replace"};
	
	
	public static int getReviewSentiment(String reviewContent) {
		int goodCount = 0, badCount = 0;
		//HashMap<String,Integer> fieldCount = new HashMap<String, Integer>();
		for(String fieldGood: UserRatingSentiment.goodSentimentFields)
			goodCount += reviewContent.split("\\s?"+fieldGood+".*?\\s").length - 1;
//			fieldCount.put(fieldGood, pageContent.split("\\s?"+fieldGood+"\\s").length - 1);
		for(String fieldBad: UserRatingSentiment.badSentimentFields)
			badCount += reviewContent.split("\\s?"+fieldBad+".*?\\s").length - 1;
//			fieldCount.put(fieldBad, pageContent.split("\\s?"+fieldBad+"\\s").length - 1);
		if(goodCount>badCount)
			return GOOD_SENTIMENT;
		
		return BAD_SENTIMENT;
	}
	
	public static String getReviewSentimentCountAsString(String reviewContent) {
		int goodCount = 0, badCount = 0;
		//HashMap<String,Integer> fieldCount = new HashMap<String, Integer>();
		for(String fieldGood: UserRatingSentiment.goodSentimentFields)
			goodCount += reviewContent.split("\\s?"+fieldGood+".*?\\s").length - 1;
//			fieldCount.put(fieldGood, pageContent.split("\\s?"+fieldGood+"\\s").length - 1);
		for(String fieldBad: UserRatingSentiment.badSentimentFields)
			badCount += reviewContent.split("\\s?"+fieldBad+".*?\\s").length - 1;
//			fieldCount.put(fieldBad, pageContent.split("\\s?"+fieldBad+"\\s").length - 1);
		return  goodCount + "," + badCount;
	}
	
	
	
	public static void main(String[] args) {
		String page = "I've had a lot of recent HD, Blu-ray version letdowns and this is certainly one of them, I won't review the movie because I assume any would be buyer is already a fan, but you should check this out since it does come with the old B&W classic original. Basically many of the scenes simply look BLURRY and out of focus, it's unavoidably noticeable and made me CRINGE in agony upon seeing such scenes in the movie. To me this is an overall letdown and I don't know if they can put out a better version... maybe it's just the nature of the source material that cannot be enhanced without also enhancing original problems with those certain camera shots?";
		System.out.println(UserRatingSentiment.SENTIMENT[UserRatingSentiment.getReviewSentiment(page)]);
		page = "The 5 stars are for the film, 0 for this edition. Universal could easily have redone the sound on the last anniversary edition, but their marketing scheme was to wait a few years and then do it. I'm not being suckered into double dipping. My edition is just fine. What will the next edition have, a thread from Montana's suit? Spare me.";
		System.out.println(UserRatingSentiment.SENTIMENT[UserRatingSentiment.getReviewSentiment(page)]);	
		page = "Since the invention of the dvd we've been waiting for this trilogy to be released, and now it finally has. But what can you say about it? It contains one of the classic heroes, one of the greatest films ever made, in fact one of the best trilogies we have and a bonus disk full of all kinds of great stuff. You have two of our best filmmakers coming together with one of our greatest actors. Classic stuff.";
		System.out.println(UserRatingSentiment.SENTIMENT[UserRatingSentiment.getReviewSentiment(page)]);	

	}
	
}
