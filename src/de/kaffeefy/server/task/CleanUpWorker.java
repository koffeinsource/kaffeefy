package de.kaffeefy.server.task;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.NamespaceManager;

import de.kaffeeshare.server.datastore.Datastore;

public class CleanUpWorker extends HttpServlet {

	private static final long serialVersionUID = 7486145088123341347L;
	private Logger log = Logger.getLogger(CleanUpWorker.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req,resp);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String para = req.getParameter("feed");
			NamespaceManager.set(para);
			
			Datastore.deleteItemsExceptLast(40); // put magic number in config
			
		} catch (Exception ex) {
			log.log(Level.SEVERE, ex.getMessage());
		}
	}
}
