package no.difi.datahotel.util.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.glaforge.i18n.io.SmartEncodingInputStream;

/**
 * An implementation of the CSVParser interface, provides methods for parsing
 * and getting info from a CSV file.
 */
public class CSVParserImpl implements CSVParser {

	private static Logger logger = Logger.getLogger(CSVParserImpl.class.getSimpleName());

	private File file;
	protected CsvReader reader;
	protected CSVMetainfo meta;

	private String[] headers = null;
	private String[] line = null;

	protected Charset charset;

	/**
	 * Constructor for CSVParserImpl
	 * 
	 * @param csvFile
	 * @throws IOException
	 */
	CSVParserImpl(File file, CSVMetainfo meta) throws IOException {
		this.file = file;
		this.meta = meta;
		this.charset = getCharset(file);

		init();
	}

	private void init() throws IOException {
		reader = new CsvReader(new FileInputStream(file), meta.getDelimiter(), charset);

		if (meta.getHeader()) {
			reader.readHeaders();
			headers = reader.getHeaders();
		}
	}

	@Override
	public String[] getHeaders() {
		return headers;
	}

	/**
	 * Gets the two first lines from the current csv file.
	 * 
	 * @return A list of String arrays
	 */
	@Override
	public List<String[]> getTwoFirstLines() throws IOException {
		List<String[]> returnList = new ArrayList<String[]>();
		CsvReader reader = new CsvReader(new FileInputStream(file), meta.getDelimiter(), charset);
		for (int i = 0; i < 2; i++) {
			reader.readRecord();
			returnList.add(reader.getValues());
		}
		reader.close();
		return returnList;
	}

	/**
	 * Returns the delimiter in use
	 * 
	 * @return char
	 */
	@Override
	public char getDelimiter() {
		return meta.getDelimiter();
	}

	/**
	 * Sets the delimiter to the wanted char
	 * 
	 * @param char
	 */
	protected void setDelimiter(char delimiter) throws IOException {
		meta.setDelimiter(delimiter);
		init();
	}

	/**
	 * Gets the next line in the CSV file and returns it in a HashMap It will
	 * start on the second line in the file the first time you use it.
	 * 
	 * @return ArrayList<String>
	 * @throws IOException
	 */
	@Override
	public Map<String, String> getNextLine() {
		Map<String, String> res = new HashMap<String, String>();

		for (int i = 0; i < line.length; i++)
			res.put(headers[i], line[i]);

		return res;
	}

	/**
	 * Gets the next line from the csv file and returns it as a string array
	 * 
	 * @return string array containing the next line in the CSV file.
	 */
	@Override
	public String[] getNextLineArray() {
		return line;
	}

	@Override
	public boolean hasNext() {
		try {
			if (reader.readRecord()) {
				line = reader.getValues();
				if (headers == null) {
					headers = new String[line.length];
					for (int i = 0; i < line.length; i++)
						headers[i] = String.valueOf(i);
				}

				return true;
			} else
			{
				close();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		return false;
	}

	@Override
	public void close() {
		reader.close();
	}

	protected Charset getCharset(File csvFile) throws IOException {
		FileInputStream fis = null;
		SmartEncodingInputStream smartIS = null;
		Charset charset = null;
		
		fis = new FileInputStream(csvFile);
		smartIS = new SmartEncodingInputStream(fis);
		charset = smartIS.getEncoding();

		smartIS.close();
		fis.close();

		return charset;
	}
}