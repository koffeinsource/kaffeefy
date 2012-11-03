package de.kaffeefy.server;

import java.util.Map;
import java.util.TreeMap;

public class RSSFeeds {
	private static Map<String, RSSFeedSource> FeedMap = null; 
	
	private RSSFeeds() {
		
	}
	
	synchronized private static void init() {
		if (FeedMap==null) {
			Map<String, RSSFeedSource> temp = new TreeMap<String, RSSFeedSource>();
			
			temp.put("motorsport-total", new RSSFeedSource("http://www.Motorsport-Total.com/rss_f1.xml", "Motorsport Total F1"));
			temp.put("heise-online", new RSSFeedSource("http://www.heise.de/newsticker/heise-atom.xml", "Heise Online"));
			temp.put("garfield", new RSSFeedSource("http://feeds.feedburner.com/uclick/garfield", "Daily Garfield"));
			temp.put("serienjunkies-de", new RSSFeedSource("http://www.serienjunkies.de/news2.rss", "Serienjunkies.de"));
			
			temp.put("sz-politik", new RSSFeedSource("http://rss.sueddeutsche.de/rss/Politik", "SZ Politik"));
			temp.put("sz-reise", new RSSFeedSource("http://www.sueddeutsche.de/app/service/rss/ressort/reise/rss.xml", "SZ Reise"));
			temp.put("sz-geld", new RSSFeedSource("http://www.sueddeutsche.de/app/service/rss/ressort/finanzen/rss.xml", "SZ Geld"));
			temp.put("sz-karriere", new RSSFeedSource("http://www.sueddeutsche.de/app/service/rss/ressort/karriere/rss.xml", "SZ Karriere"));
			temp.put("sz-kultur", new RSSFeedSource("http://www.sueddeutsche.de/app/service/rss/ressort/kultur/rss.xml", "SZ Kultur"));
			temp.put("sz-digital", new RSSFeedSource("http://www.sueddeutsche.de/app/service/rss/ressort/computerwissen/rss.xml", "SZ Digital"));
			temp.put("sz-wissen", new RSSFeedSource("http://www.sueddeutsche.de/app/service/rss/ressort/wissen/rss.xml", "SZ Wissen"));
			temp.put("sz-wirtschaft", new RSSFeedSource("http://www.sueddeutsche.de/app/service/rss/ressort/wirtschaft/rss.xml", "SZ Wirtschaft"));
			
			FeedMap = temp;
		}
	}
	
	synchronized public static Map<String, RSSFeedSource> get() {
		init();
		return FeedMap;
	}
}
