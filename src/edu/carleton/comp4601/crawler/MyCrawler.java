package edu.carleton.comp4601.crawler;

import edu.carleton.comp4601.graph.*;
import edu.carleton.comp4601.repository.MyMongoClient;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.*;


public class MyCrawler extends WebCrawler {
	Vertex v, w;
	long crawltime = 0;
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|pptx|png|ico"
			+ "|mp3|mp4|zip|gz))$");
	
	MyMongoClient mc = new MyMongoClient();
	DB database = mc.getDB();
	DBCollection reviews = database.getCollection("review");
	
	/**
	 * This method receives two parameters. The first parameter is the page
	 * in which we have discovered this new url and the second parameter is
	 * the new url. You should implement this function to specify whether
	 * the given url should be crawled or not (based on your crawling logic).
	 * In this example, we are instructing the crawler to ignore urls that
	 * have css, js, git, ... extensions and to only accept urls that start
	 * with "http://www.ics.uci.edu/". In this case, we didn't need the
	 * referringPage parameter to make the decision.
	 */

	@Override
	protected boolean shouldFollowLinksIn(WebURL url) {
		crawltime = System.currentTimeMillis();
		return super.shouldFollowLinksIn(url);
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		crawltime = System.currentTimeMillis();
		return !FILTERS.matcher(href).matches() &&
				 (href.startsWith("https://sikaman.dyndns.org/courses/4601/assignments/training/pages/"));
	}

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	@Override
	public void visit(Page page) {
		
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);
		
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			v = CrawlGraph.getInstance().getVertex(page.getWebURL().getURL());
			if (v == null) {
				v = new Vertex(page.getWebURL().getURL(), page.getWebURL().getDocid());
				CrawlGraph.getInstance().addVertex(v);
			}
				
			for (WebURL urlLinks: links) {
				if(this.shouldVisit(page,urlLinks)){
					w = CrawlGraph.getInstance().getVertex(urlLinks.getURL());
					if(w == null) {
						w = new Vertex(urlLinks.getURL(), urlLinks.getDocid());
						CrawlGraph.getInstance().addVertex(w);
					}
					
					CrawlGraph.getInstance().addEdge(v, w);
				}
			}

			Document document = Jsoup.parse(htmlParseData.getHtml());
			String expression = document.html().toString().replace("\n", "");

			// Regular Expression to get usernames and reviews
			Matcher p = Pattern.compile("(<a href=.*?</a>)\\s*<br>\\s*<p>(.*?)</p>\\s*<br>").matcher(expression);
		
			while (p.find()) {
				String user = p.group(1);
				String review = p.group(2);
				int startIndex = user.indexOf(">")+1;
				int endIndex = user.indexOf("</a>");
				user = user.substring(startIndex, endIndex);
			
				BasicDBObject query = new BasicDBObject();
				query.put("docId", page.getWebURL().getDocid());
				
				String pageTitle = document.title().toString();
				
				// add the review in
				if (pageTitle.startsWith("B") || Character.isDigit(pageTitle.charAt(0))) {
					BasicDBObject reviewToAdd = new BasicDBObject();
					reviewToAdd.put("docId", page.getWebURL().getDocid());
					reviewToAdd.put("movie", document.title().toString());
					reviewToAdd.put("user", user);
					reviewToAdd.put("review", review);
					
					reviews.insert(reviewToAdd);
				}
			}
		}
	}
	
}