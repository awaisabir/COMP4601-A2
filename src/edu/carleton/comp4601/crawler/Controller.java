package edu.carleton.comp4601.crawler;

import java.util.HashSet;

import com.mongodb.BasicDBObject;

import Jama.Matrix;
import edu.carleton.comp4601.categories.Categorizer;
import edu.carleton.comp4601.categories.UserCommunityFinder;
import edu.carleton.comp4601.graph.*;
import edu.carleton.comp4601.repository.Marshaller;
import edu.carleton.comp4601.repository.MyMongoClient;
import edu.carleton.comp4601.userdata.User;
import edu.carleton.comp4601.userdata.UserCollection;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	static int current = 0;
	
	public Controller (String[] seeds) throws Exception {
		String crawlStorageFolder = "/data/crawl/root";
		int numberOfCrawlers = 10;
		
		CrawlGraph.setInstance(new CrawlGraph());
		SocialNetwork.setInstance(new SocialNetwork());
		
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		// forces other files to be crawled
		config.setIncludeBinaryContentInCrawling(true);
		// limits their size
		config.setMaxDownloadSize(10000000);
		
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		//adds seeds
		for(int i=0; i < seeds.length; i++){
			try {
				controller.addSeed(seeds[i]);
			} catch (Exception e) {	e.printStackTrace();}
		}
		
		//starts the controller to create the new graph
		controller.start(MyCrawler.class, numberOfCrawlers);
		
		MyMongoClient.getInstance().updateInCollection(
				"COMP4601-A2",
				"graph",
				new BasicDBObject(), 
				new BasicDBObject("socialNetwork",Marshaller.serializeObject(SocialNetwork.getInstance())).append("reviewsGraph",Marshaller.serializeObject(CrawlGraph.getInstance())));
		
		Categorizer c = new Categorizer();
	}
	
	/*change the sites crawled
	static String[] seeds = {
		"https://sikaman.dyndns.org/courses/4601/assignments/training/pages/", 
		"https://sikaman.dyndns.org/courses/4601/assignments/training/graph/"
	};*/

	public static void main(String[] args) throws Exception {
		String[] seeds = {"https://sikaman.dyndns.org/courses/4601/assignments/training/pages/", "https://sikaman.dyndns.org/courses/4601/assignments/training/graph/"};
		new Controller(seeds);
	}

}