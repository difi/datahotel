package no.difi.datahotel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvReader;

/**
 * Make sure to always provide the same environment for reading CSV-files.
 */
public class CSVReader {

	private static Logger logger = Logger.getLogger(CSVReader.class.getSimpleName());

	private FileInputStream fileReader;
	private CsvReader csvReader;

	private String[] headers = null;
	private String[] line = null;

	protected Charset charset = Charset.forName("UTF-8");

	/**
	 * Allow to create an instance to act as a "factory".
	 */
	public CSVReader() {
		
	}
	
	public CSVReader open(File file) throws IOException {
		return new CSVReader(file);
	}
	
	/**
	 * Constructor for CSVParserImpl
	 * 
	 * @param csvFile
	 * @throws IOException
	 */
	public CSVReader(File file) throws IOException {
		fileReader = new FileInputStream(file);
		
		csvReader = new CsvReader(fileReader, charset);
		csvReader.setDelimiter(';');
		csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
		
		csvReader.readHeaders();
		this.headers = csvReader.getHeaders();
	}

	public String[] getHeaders() {
		return headers;
	}

	/**
	 * Gets the next line in the CSV file and returns it in a HashMap It will
	 * start on the second line in the file the first time you use it.
	 * 
	 * @return ArrayList<String>
	 * @throws IOException
	 */
	public Map<String, String> getNextLine() throws Exception {
		Map<String, String> res = new HashMap<String, String>();

		try {
			for (int i = 0; i < line.length; i++)
				res.put(headers[i], line[i]);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(String.valueOf(res));
		}

		return res;
	}

	/**
	 * Gets the next line from the csv file and returns it as a string array
	 * 
	 * @return string array containing the next line in the CSV file.
	 */
	public String[] getNextLineArray() {
		return line;
	}

	public boolean hasNext() {
		try {
			if (csvReader.readRecord()) {
				line = csvReader.getValues();
				return true;
			} else
			{
				this.close();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		return false;
	}

	public void close() {
		csvReader.close();

		try {
			fileReader.close();
		} catch (IOException e) {
			logger.log(Level.INFO, e.getMessage());
		}
	}
}
