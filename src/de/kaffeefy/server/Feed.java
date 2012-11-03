package de.kaffeefy.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.NamespaceManager;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

import de.kaffeeshare.server.datastore.Datastore;
import de.kaffeeshare.server.datastore.Item;

/**
 * This servlet generates a rss feed with the latest news.
 */
public class Feed extends HttpServlet {

	private static final long serialVersionUID = -5819674729148390595L;

	private Logger log = Logger.getLogger(Feed.class.getName());
	
	/**
	 * Called when the feed url is requested.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String para = req.getParameter("feed");
			NamespaceManager.set(para);
			
			resp.setContentType("text; charset=UTF-8");
			String feedType = "rss_2.0";

			SyndFeed feed = new SyndFeedImpl();
			feed.setFeedType(feedType);

			RSSFeedSource r = RSSFeeds.get().get(para);
			feed.setTitle(r.getName());
			feed.setLink("http://"+req.getServerName()+"/feed?feed="+para);
			feed.setDescription(Config.Phrase);

			List<SyndEntry> feedEntries = new ArrayList<SyndEntry>();
			List<Item> items = Datastore.getItems(20);
			for (Item item : items) {
				SyndEntry feedEntry;
				SyndContent feedContent;

				feedEntry = new SyndEntryImpl();
				feedEntry.setTitle(item.getCaption());
				feedEntry.setLink(item.getUrl());
				feedEntry.setPublishedDate(item.getCreatedAt());
				feedContent = new SyndContentImpl();
				feedContent.setType("html");
				String content = "";
				String imageUrl = item.getImageUrl();
				if (imageUrl != null) {
					content += "<div style=\"float:left; margin-right:16px; margin-bottom:16px;\"><img width=\"150\" src=\""
							+ imageUrl + "\" alt=\"\"/></div>";
				}
				// escaped in validator - why?
				content += "<p>" + item.getDescription() + "</p>";
				content += " <a href=\"" + item.getUrl();
				content += "\">&raquo; " + "Link" + "</a>";
				feedContent.setValue(content);
				feedEntry.setDescription(feedContent);
				feedEntries.add(feedEntry);
			}

			feed.setEntries(feedEntries);

			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, resp.getWriter());
			resp.getWriter().close();
		} catch (Exception ex) {
			log.log(Level.SEVERE, ex.getMessage());
		}
	}

}