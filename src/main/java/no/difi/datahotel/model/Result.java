package no.difi.datahotel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Representation of a CSV File
 */
public class Result implements Serializable {

	private static final long serialVersionUID = -8412531397903068046L;
	private List<Map<String, String>> entries;
	private Long page = 0L, pages = 0L, posts = 0L;

	public Result() {
		this.setEntries(new ArrayList<Map<String, String>>());
	}

	/**
	 * Creates a new CSVData object with the specified list of hashmaps.
	 * 
	 * @param entries
	 *            A list of hashmaps. Each entry in the arraylist must be a line
	 *            in the CSV File, each entry in the hashmap must be column
	 *            header and respective value.
	 */
	public Result(List<Map<String, String>> entries) {
		this.setEntries(entries);
	}

	/**
	 * Sets the CSV data.
	 * 
	 * @param entries
	 *            CSV data.
	 */
	public void setEntries(List<Map<String, String>> entries) {
		this.entries = entries != null ? entries : new ArrayList<Map<String, String>>();
	}

	/**
	 * Gets the CSV data.
	 * 
	 * @return Returns the CSV data.
	 */
	public List<Map<String, String>> getEntries() {
		return entries;
	}

	public Long getPosts() {
		return posts;
	}

	public void setPosts(long posts) {
		this.posts = posts;
		this.pages = ((posts - (posts % 100)) / 100) + (posts % 100 == 0 ? 0 : 1);
	}

	public Long getPages() {
		return pages;
	}

	public Long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}
}