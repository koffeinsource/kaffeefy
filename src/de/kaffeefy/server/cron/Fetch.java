package de.kaffeefy.server.cron;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import de.kaffeefy.server.RSSFeedSource;
import de.kaffeefy.server.RSSFeeds;
import de.kaffeeshare.server.datastore.Datastore;
import de.kaffeeshare.server.datastore.Item;
import de.kaffeeshare.server.exception.InputErrorException;

public class Fetch extends HttpServlet {
	private static final long serialVersionUID = -4981597569972263802L;

	private Logger log = Logger.getLogger(Fetch.class.getName());
	private static final URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
	
	private static Set<String> urlSet = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		String para = req.getParameter("feed");
		NamespaceManager.set(para);

		RSSFeedSource source = RSSFeeds.get().get(para);
		String urlString = source.getUrl();

		URL feedURL = new URL(urlString);
		XmlReader reader = new XmlReader(feedURL);
		
		try {
			SyndFeed feed = new SyndFeedInput().build(reader);
			
			@SuppressWarnings("unchecked")
			List<SyndEntry> entries = feed.getEntries();
			
			List<Item> items = new ArrayList<Item>();
			
			for (SyndEntry s : entries) {
				log.info("found " + s.getUri());
				
				if (urlSet.contains(s.getUri())) {
					log.info("url already in the local set, skip");
					continue;
				}
				
				URL url = new URL(s.getUri());
								
				URL finalUrl = null;
								
				Item item = null;
				try {
					for (int i=0; i<50; ++i) {
						
						HTTPResponse HTTPresp = null;
						try {
							HTTPresp = urlFetchService.fetch(url);
							finalUrl = HTTPresp.getFinalUrl();
							
							if (finalUrl == null) finalUrl = url;
							item = new Item(finalUrl);
						} catch (IOException e) {
							continue;
						}
												
						break;
					}
				} catch (InputErrorException e) {
					// means URL is not a real url
					urlSet.add(s.getUri());
					continue;
				}
				
				if (s.getPublishedDate() != null)
					item.setCreatedAt(s.getPublishedDate());
				
				if (!Datastore.isStored(item)) items.add(item);
				
				urlSet.add(s.getUri());
			}
			
			log.info("items size = " + items.size());
			
			Datastore.storeItems(items);
			
			if (items.size() > 0) {
				// notify google
				URL ping = new URL("http://blogsearch.google.com/ping?" +
						"url=http://kaffeefy.appspot.com/&" +
						"changesURL=http://kaffeefy.appspot.com/feed?feed="+para
						);
				
				urlFetchService.fetch(ping);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
