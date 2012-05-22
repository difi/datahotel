package no.difi.datahotel.util.csv;

import java.io.File;
import java.io.IOException;

import javax.naming.directory.InvalidAttributesException;

public class CSVParserFactory {
	
	/**
	 * Creates a new CSVParser object based on the file you provide, with the
	 * standard ';' delimiter
	 * @param file - csv file
	 * @return CSVParser object
	 * @throws IOException - If the file can't be opened or is missing
	 */
	public static CSVParser getCSVParser(File file) throws IOException {
		return new CSVParserImpl(file, new CSVMetainfo(';', true, true));
	}
	
	/**
	 * Makes users testable.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public CSVParser get(File file) throws IOException {
		return new CSVParserImpl(file, new CSVMetainfo(';', true, true));
	}
	
	/**
	 * Creates a new CSVParser object based on the file you provide, this method
	 * will try to find the delimiter used in the file, as long as it is one of the
	 * following: ';' ',' ':' or tab
	 * @param csv file
	 * @return CSVParser object
	 * @throws IOException - If the file can't be opened or is missing
	 */
	public static CSVParser getCSVParserSmart(File file) throws IOException {
		return new CSVParserSmart(file, new CSVMetainfo(',', true, true));
	}
	
	/**
	 * Creates a a new CSVParser object based on the file you provide, this method 
	 * will try to find the delimiter, it will also try to find errors in the CSV file.
	 * If it finds no errors it closes the parser.
	 * @param csv file
	 * @throws InvalidAttributesException - Gets thrown if the file contains an error, the message will say which line of the file contains the error
	 * @throws IOException - If the file can't be opened or is missing
	 */
	public static void getCSVParserErrorCheck(File file) throws InvalidAttributesException, IOException {
		CSVParser error = new CSVParserSmart(file, true);
		error.close();
	}
}
