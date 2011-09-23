package no.difi.datahotel.util.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.naming.directory.InvalidAttributesException;

/**
 * Class that extends CSVParserImpl and provides a constructor that find the delimiter for you.
 * Should not be used directly. You should use the CSVParserFactory to create your parser objects.
 */
public class CSVParserSmart extends CSVParserImpl {

	/**
	 * Use the CSVParserFactory to make CSVParser objects
	 * Constructor that makes a "smart" CSVParser object, it will find the most likely delimiter
	 * @param csvFile - file to be parsed
	 * @param metainfo - 
	 * @throws IOException
	 * @throws InvalidAttributesException 
	 */
	public CSVParserSmart(File csvFile, CSVMetainfo metainfo) throws IOException {
		super(csvFile, metainfo);
		InputStream inStream = new FileInputStream(csvFile);
		charset = FindCsvErrors.findCharset(inStream);
		inStream.close();
		inStream = new FileInputStream(csvFile);
		InputStreamReader inReader = new InputStreamReader(inStream, charset);
		BufferedReader bufReader = new BufferedReader(inReader);
		char delimiter = FindCsvErrors.findDelimiter(bufReader).toCharArray()[0];
		metainfo.setDelimiter(delimiter);
		super.setDelimiter(delimiter);
	}

	public CSVParserSmart(File csvFile, boolean checkErrors) throws IOException, InvalidAttributesException {
		super(csvFile, new CSVMetainfo());
		InputStream inStream = new FileInputStream(csvFile);
		charset = FindCsvErrors.findCharset(inStream);
		inStream.close();
		inStream = new FileInputStream(csvFile);
		InputStreamReader inReader = new InputStreamReader(inStream, charset);
		BufferedReader bufReader = new BufferedReader(inReader);
		char delimiter = FindCsvErrors.findDelimiter(bufReader).toCharArray()[0];
		super.setDelimiter(delimiter);
	}
	
	
}
