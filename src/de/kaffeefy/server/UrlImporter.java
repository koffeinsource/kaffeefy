package de.kaffeefy.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.kaffeeshare.server.datastore.Datastore;
import de.kaffeeshare.server.datastore.Item;
import de.kaffeeshare.server.exception.InputErrorException;
import de.kaffeeshare.server.plugins.DefaultPlugin;
import de.kaffeeshare.server.plugins.Garfield;

/**
 * Imports a URL into our DB. The code calls plugins to
 * extract the data from the homepage. 
 */
public class UrlImporter {
	
	private static final Logger log = Logger.getLogger(UrlImporter.class.getName());
	
	/**
	 * Adds the first URL of a String to the DB
	 * @param text plain text to be parsed
	 * @return null if no URL is in the text, otherwise the url
	 * @throws IOException 
	 */
	public static void importFromText(String text) throws IOException {
		String url = getURLPlain(text);
		
		if (url != null) {
			log.info("Try to add url " + url + " to DB");
			Item item;
			try {
				item = new Item(new URL(url));
			} catch (MalformedURLException e) {
				throw new InputErrorException();
			}
			Datastore.storeItem(item);
		}
		
		//return url;
	}
	
	/**
	 * Adds the first URL of a String to the DB
	 * @param text html to be parsed
	 * @return null if no URL is in the text, otherwise the url
	 * @throws IOException 
	 */
	public static void importFromHTML(String text) throws IOException {
		String url = getURLHTML(text);
		
		if (url != null) {
			log.info("Try to add url " + url + " to DB");
			Item item;
			try {
				item = new Item(new URL(url));
			} catch (MalformedURLException e) {
				throw new InputErrorException();
			}
			Datastore.storeItem(item);
		}
		
		//return url;
	}
	
	/**
	 * Generates an item from a url.
	 * @throws IOException 
	 */
	public static void fetchUrl(URL url, Item item) throws IOException {
		//check for plugin matches
		Garfield gar = new Garfield();
		if (gar.match(url.toString())) gar.creatItem(url, item);
		else new DefaultPlugin().creatItem(url, item);
	}
	
	/**
	 * URL pattern, public domain.
	 */
	static private Pattern urlPatternPlain = Pattern.compile(
	                        "\\b((http(s?)\\:\\/\\/|~\\/|\\/)|www.)" + 
	                        "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + 
	                        "|mil|biz|info|mobi|name|aero|jobs|museum" + 
	                        "|travel|[a-z]{2}))(:[\\d]{1,5})?" + 
	                        "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + 
	                        "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
	                        "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + 
	                        "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
	                        "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + 
	                        "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b"
	                       );

	/**
	 * Extracts the first URL from a given Strings
	 */
	static private String getURLPlain(String text) {
        Matcher m = urlPatternPlain.matcher(text);
		
		while(m.find()) {
			String url = m.group();
			if (url.startsWith("(") && url.endsWith(")")) {
				url = url.substring(1, url.length() - 1);
			}
			
			log.info("found url: " + url);
			return url;
		}
		return null;
		
	}
	
	static private Pattern urlPatternHTML = Pattern.compile(
	                        "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))"
	                       );
	
	/**
	 * Extracts the first URL from given HTML code
	 */
	static private String getURLHTML(String html) {
		Matcher m = urlPatternHTML.matcher(html);
		
		while(m.find()) {
			String url = m.group();
			
			log.info("found url: " + url);
			return url;
		}
		
		return null;
	}
}
