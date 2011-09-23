package no.difi.datahotel.util.csv;

public class CSVMetainfo {

	private char delimiter = ';';
	private Boolean quoted = null;
	private boolean header = true;

	/**
	 * Standard constructor that takes no arguments, defaults to delimiter = ';'
	 * and quoted = null.
	 */
	public CSVMetainfo() {
	}

	/**
	 * Constructor that takes in both a custom delimiter and whether or not the
	 * CSV is quoted
	 * 
	 * @param delimiter
	 *            - The delimiter the CSV file uses, null if you don't know
	 * @param quoted
	 *            - true if the file is quoted, false otherwise
	 */
	public CSVMetainfo(char delimiter, Boolean quoted, boolean header) {
		this.delimiter = delimiter;
		this.quoted = new Boolean(quoted);
		this.header = header;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public Boolean getQuoted() {
		return quoted;
	}

	public void setQuoted(Boolean quoted) {
		this.quoted = quoted;
	}

	public boolean getHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}
}
