package de.kaffeeshare.server.datastore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;

import de.kaffeefy.server.UrlImporter;


/**
 * A news item.
 */
public class Item {

	private static final String DB_KIND_ITEM = "Item";
	private static final String DB_ITEM_CAPTION = "Caption";
	private static final String DB_ITEM_URL = "URL";
	private static final String DB_ITEM_DESCRIPTION = "Description";
	private static final String DB_ITEM_CREATEDAT = "CreatedAt";
	private static final String DB_ITEM_IMAGEURL = "imageUrl";
	
	private String caption;
	private String url;
	private String imageUrl;
	private String description;
	private Date createdAt;

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	private Item() {
		this.createdAt = new Date();
	}
	/**
	 * Creates a new item.
	 */
	public Item(String caption, String url, String description, String imageUrl) {
		this();
		this.caption = caption;
		setUrl(url);
		this.description = description;
		this.imageUrl = imageUrl;
	}
	
	/**
	 * Creates an item based on a URL
	 * @throws IOException 
	 */
	public Item(URL url) throws IOException {
		this();
		
		// generate item based on URL
		UrlImporter.fetchUrl(url, this);
	}
	
	/**
	 * Creates an item from a DB entity.
	 */
	public Item(Entity e) {
		this ((String) e.getProperty(DB_ITEM_CAPTION),
		      (String) e.getProperty(DB_ITEM_URL),
		      ((Text) e.getProperty(DB_ITEM_DESCRIPTION)).getValue(),
		      (String) e.getProperty(DB_ITEM_IMAGEURL)
		     );
		
		this.createdAt = new Date((Long)e.getProperty(DB_ITEM_CREATEDAT));
	}
	

	/**
	 * Returns a DB entity for the current item.
	 */
	public Entity toEntity() {
		Entity entity = new Entity(getDBKey());
		entity.setUnindexedProperty(DB_ITEM_CAPTION, getCaption());
		entity.setProperty(DB_ITEM_URL, getUrl());
		entity.setUnindexedProperty(DB_ITEM_DESCRIPTION, new Text(getDescription()));
		entity.setUnindexedProperty(DB_ITEM_IMAGEURL, getImageUrl());
		entity.setProperty(DB_ITEM_CREATEDAT, createdAt.getTime());
		
		return entity;
	}
	
	/**
	 * The DB key of the current item.
	 */
	public Key getDBKey() {
		return KeyFactory.createKey(DB_KIND_ITEM, url);
	}
	
	/**
	 * Creates a DB query returning items ordered by creation date.
	 */
	static public Query getDBQuery() {
		Query query = new Query(DB_KIND_ITEM, null);
		query.addSort(DB_ITEM_CREATEDAT, SortDirection.DESCENDING);
		return query;
	}
	
	public Query isPresentQuery() {
		Query query = new Query(DB_KIND_ITEM, null);
		query.addFilter(DB_ITEM_URL, Query.FilterOperator.EQUAL, url);
		query.setKeysOnly();
		return query;
	}
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		try {
			// called to make sure this is a valid URL
			url.toURI();
			this.url = url.toString();
		} catch (URISyntaxException e) {
			this.url = null;
		}
	}
	
	public void setUrl(String url) {
		URL temp;
		try {
			temp = new URL(url);
			setUrl(temp);
		} catch (MalformedURLException e) {
			this.url = null;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}