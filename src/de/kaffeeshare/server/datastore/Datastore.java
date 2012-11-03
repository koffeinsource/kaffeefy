package de.kaffeeshare.server.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/**
 * Datastore helper class.
 */
public class Datastore {

	private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	/**
	 * Stores an item in the DB.
	 */
	public static void storeItem(Item item) {
		Entity entity = item.toEntity();
		datastore.put(entity);
	}
	
	/**
	 * Stores multiple items in the DB at once.
	 */
	public static void storeItems(List<Item> items) {
		List<Entity> entities = new ArrayList<Entity>();
		
		for (Item i : items)
			entities.add(i.toEntity());
		
		datastore.put(entities);
	}

	/**
	 * Deletes an item in DB, currently not used and only kept for reference.
	 */
	@SuppressWarnings("unused")
	private static void deleteItem(Item item) {
		Key key = item.getDBKey();
		datastore.delete(key);
	}
	
	public static boolean isStored(Item item) {
		PreparedQuery pq = datastore.prepare(item.isPresentQuery());
		Collection<Entity> entities = pq.asList(FetchOptions.Builder.withDefaults());
		return !entities.isEmpty();
	}

	/**
	 * Gets the newest @param maxNumber item from DB.
	 */
	public static List<Item> getItems(int maxNumber) {
		PreparedQuery pq = datastore.prepare(Item.getDBQuery());
		Collection<Entity> entities = pq.asList(FetchOptions.Builder.withLimit(maxNumber));
		return getItems(entities);
	}
	
	public static void deleteItemsExceptLast(int ignoreNumer) {
		Query query = Item.getDBQuery();
		query.setKeysOnly();
		PreparedQuery pq = datastore.prepare(query);
		Collection<Entity> entities = pq.asList(FetchOptions.Builder.withOffset(ignoreNumer));
		List<Key> keys = new ArrayList<Key>();
		for (Entity e : entities) {
			keys.add(e.getKey());
		}
		datastore.delete(keys);
	}

	private static List<Item> getItems(Collection<Entity> entities) {
		List<Item> items = new ArrayList<Item>();
		for (Entity entity : entities) {
			items.add(new Item(entity));
		}
		return items;
	}
}