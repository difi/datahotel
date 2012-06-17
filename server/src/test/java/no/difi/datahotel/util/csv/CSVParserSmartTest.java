package no.difi.datahotel.util.csv;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class CSVParserSmartTest {
	
	private File csvFile;
	private CSVParser testObject;
	URL url;
	char delimiter;

	@Test
	public void testCSVParserSmart() {
		url = this.getClass().getResource("/csv/ANSII_csv.csv");
		csvFile = new File(url.getFile());
		delimiter = ',';
		try {
			// Tests to see if it can find , as delimiter
			testObject = CSVParserFactory.getCSVParserSmart(csvFile);
			assertEquals("Delimiter: " + testObject.getDelimiter(),delimiter, testObject.getDelimiter());
			testObject.close();
			
			// Tests to see if it can find : as the delimiter
			url = this.getClass().getResource("/csv/ANSII_csv_colon.csv");
			csvFile = new File(url.getFile());
			delimiter = ':';
			testObject = CSVParserFactory.getCSVParserSmart(csvFile);
			assertEquals("Delimiter: " + testObject.getDelimiter(),delimiter, testObject.getDelimiter());
			testObject.close();
			
			// Tests to see if it can find ; as the delimiter
			url = this.getClass().getResource("/csv/ANSII_csv_semicolon.csv");
			csvFile = new File(url.getFile());
			delimiter = ';';
			testObject = CSVParserFactory.getCSVParserSmart(csvFile);
			assertEquals("Delimiter: " + testObject.getDelimiter(),delimiter, testObject.getDelimiter());		
			testObject.close();
			
			// Tests to see if it can find tab as delimiter
			url = this.getClass().getResource("/csv/ANSII_csv_tab.csv");
			csvFile = new File(url.getFile());
			delimiter = 9;
			testObject = CSVParserFactory.getCSVParserSmart(csvFile);
			assertEquals("Delimiter: " + testObject.getDelimiter(),delimiter, testObject.getDelimiter());
			testObject.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
