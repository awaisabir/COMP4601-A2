package edu.carleton.comp4601.crawler;

import edu.carleton.comp4601.graph.*;

import java.util.regex.Pattern;


import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.url.*;


public class MyCrawler extends WebCrawler {
	Vertex v, w;
	long crawltime = 0;
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|pptx|png|ico"
			+ "|mp3|mp4|zip|gz))$");

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
		// TODO Auto-generated method stub
		crawltime = System.currentTimeMillis();
		return super.shouldFollowLinksIn(url);
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		crawltime = System.currentTimeMillis();
		return !FILTERS.matcher(href).matches() &&
				 (href.startsWith("https://sikaman.dyndns"));
	}

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	@Override
	public void visit(Page page) {
//		System.out.println("Time Before: " + crawltime);
//		crawltime = (System.currentTimeMillis() - crawltime);
//		System.out.println("Time After: " + crawltime);
//		getMyController().getConfig().setPolitenessDelay((int)(crawltime*10l));
//		String url = page.getWebURL().getURL();
//		System.out.println("URL: " + url);
//
//		BasicDBObject fileContent = new BasicDBObject();
//		String content = "";
//		if (page.getParseData() instanceof HtmlParseData) {
//			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
//			Set<WebURL> links = htmlParseData.getOutgoingUrls();
//
//			v = CrawlGraph.getInstance().getVertex(page.getWebURL().getURL());
//			if (v == null) {
//				v = new Vertex(page.getWebURL().getURL(), page.getWebURL().getDocid());
//				CrawlGraph.getInstance().addVertex(v);
//			}
//				
//			for (WebURL urlLinks: links) {
//				if(this.shouldVisit(page,urlLinks)){
//					w = CrawlGraph.getInstance().getVertex(urlLinks.getURL());
//					if(w == null) {
//						w = new Vertex(urlLinks.getURL(), urlLinks.getDocid());
//						CrawlGraph.getInstance().addVertex(w);
//					}
//					CrawlGraph.getInstance().addEdge(v, w);
//				}
//			}
//			System.out.println(CrawlGraph.getInstance().getGraph().vertexSet().size());
//			Document document = Jsoup.parse(htmlParseData.getHtml());
//			String selector = "img[src~=(?i)\\.(png|jpe?g|gif)]";
//			Elements images = document.select(selector);
//			Elements theLinks = document.select("a[href]");
//			Elements text = document.getElementsByTag("p");
//			text.addAll(document.select("h1"));
//			text.addAll(document.select("h2"));
//			text.addAll(document.select("h3"));
//			text.addAll(document.select("h4"));
//			text.addAll(document.select("h5"));	
//			text.addAll(document.select("title"));	
//			
//			fileContent.put("links", theLinks.toString());
//			fileContent.put("images", images.toString());
//			content =  text.toString();
//			fileContent.put("crawlTime", crawltime);
//		}
//			Tika tika = new Tika();
//			Metadata metadata = new Metadata();
//			ParseContext context = new ParseContext();
//			Parser parser = new AutoDetectParser();
//			BodyContentHandler handler = new BodyContentHandler();
//			byte[] data = page.getContentData();
//			String type = tika.detect(data);
//			System.out.println(type);
//			ByteArrayInputStream stream = new ByteArrayInputStream(data);
//
//			try {
//				parser.parse(stream, handler, metadata, context);
//				if(content.equals(""))
//					content = handler.toString();
//				BasicDBObject headerContent = new BasicDBObject();
//				BasicDBObject metadataContent = new BasicDBObject();
//				for(String name: metadata.names())
//					metadataContent.put(name,metadata.get(name));
//				for(Header header: page.getFetchResponseHeaders())
//					headerContent.put(header.getName(),header.getValue());
//				
//				fileContent.put("metadata", metadataContent);
//				fileContent.put("HeaderContent", headerContent);
//				fileContent.put("data", data);
//				fileContent.put("content", content);
//				fileContent.put("docID", page.getWebURL().getDocid());
//				fileContent.put("url", url);
//				fileContent.put("date", LocalDateTime.now().toString());
//				
//				//String r =  tika.parseToString(new ByteArrayInputStream(page.getContentData()));
//				BasicDBObject docWithID = new BasicDBObject("docID", page.getWebURL().getDocid());
//			
//				MyMongoClient.getInstance().updateInCollection("Crawler", "DocumentData", docWithID, fileContent);
//				
//			} catch (Exception e){
//				System.out.println(e.toString());
//			}

		//crawltime = System.currentTimeMillis();
	}
	
}