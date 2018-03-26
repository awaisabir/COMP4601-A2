package edu.carleton.comp4601.crawler;

import edu.carleton.comp4601.graph.*;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;

import java.util.HashMap;

import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class UserController {
	static int current = 0;
	
	//We will start from the first user on the graph page, assuming we will visit all users 
	static String[] seeds = {"https://sikaman.dyndns.org/courses/4601/assignments/training/graph/A1A69DJ2KPU4CH.html"};

	public static void control() throws Exception {
		String crawlStorageFolder = "/data/crawl/root";
		int numberOfCrawlers = seeds.length;
		CrawlGraph.setInstanceUser(new CrawlGraph());
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setMaxPagesToFetch(2);
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
		controller.start(UserCrawler.class, numberOfCrawlers);

	}

}