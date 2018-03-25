package edu.carleton.comp4601.crawler;

import edu.carleton.comp4601.graph.*;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	static int current = 0;
	//change the sites crawled
	static String[] seeds = {"https://sikaman.dyndns.org/courses/4601/assignments/training/pages/0767800117.html"};

	public static void main(String[] args) throws Exception {
		String crawlStorageFolder = "/data/crawl/root";
		int numberOfCrawlers = seeds.length;
		CrawlGraph.setInstance(new CrawlGraph());
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

	}

}