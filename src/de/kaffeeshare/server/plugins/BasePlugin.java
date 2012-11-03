package de.kaffeeshare.server.plugins;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;

import de.kaffeeshare.server.datastore.Item;

/**
 * Abstract base class for all plugins
 */
public abstract class BasePlugin {

	protected static final Logger log = Logger.getLogger(BasePlugin.class.getName());
	
	public abstract boolean match(String url);
	
	/**
	 * Will fill item with data from the url
	 */
	public abstract void creatItem(URL url, Item item) throws IOException;
	
	protected String getProperty(Document doc, String prop) {
		String caption;
		caption = doc.getElementsByAttributeValue("property", prop)
		             .first().attr("content");
		return caption;
	}
}
