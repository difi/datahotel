package no.difi.datahotel.util.jersey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Representation of a CSV File 
 */
public class CSVData implements Serializable {
	
	private static final long serialVersionUID = -8412531397903068046L;
	private ArrayList<Map<String, String>> entries;
	
	/**
	 * Creates a new CSVData object with the specified list of hashmaps.
	 * @param entries A list of hashmaps. Each entry in the arraylist must be a line in the
	 * CSV File, each entry in the hashmap must be column header and respective value.
	 */
	public CSVData(ArrayList<Map<String, String>> entries) {
		this.setEntries(entries);
	}
	
	/**
	 * Sets the CSV data.
	 * @param entries CSV data.
	 */
	public void setEntries(ArrayList<Map<String, String>> entries) {
		this.entries = entries;
	}

	/**
	 * Gets the CSV data.
	 * @return Returns the CSV data.
	 */
	public ArrayList<Map<String, String>> getEntries() {
		return entries;
	}
}