package de.kaffeefy.server.cron;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import de.kaffeefy.server.RSSFeedSource;
import de.kaffeefy.server.RSSFeeds;

public class CleanUp extends HttpServlet {

	private static final long serialVersionUID = 2005438033197574975L;
	
	private Logger log = Logger.getLogger(CleanUp.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			
			Iterator<Entry<String, RSSFeedSource>> it = RSSFeeds.get().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, RSSFeedSource> pair = it.next();
				String key = pair.getKey();
				
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(TaskOptions.Builder.withUrl("/task/cleanup").param("feed", key));
				log.info("Data added to clean up worker " + key);
			}

			

		} catch (Exception ex) {
			log.log(Level.SEVERE, ex.getMessage());
		}
	}

}
