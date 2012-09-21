package eu.scapeproject.hrider.service.indexer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "search-result")
public class SearchResult {
	@XmlElement
	private final String href;
	@XmlElement
	private final String title;
	@XmlElement
	private final String details;

	protected SearchResult(String href, String title, String details) {
		super();
		this.href = href;
		this.title = title;
		this.details = details;
	}

	public String getHref() {
		return href;
	}

	public String getTitle() {
		return title;
	}

	public String getDetails() {
		return details;
	}

}
