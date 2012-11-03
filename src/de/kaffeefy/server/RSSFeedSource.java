package de.kaffeefy.server;

public class RSSFeedSource {
	private String url;
	
	private String name;

	public RSSFeedSource(String url, String name) {
		super();
		this.url = url;
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}
}
