package no.difi.datahotel.util.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import javax.naming.directory.InvalidAttributesException;

import no.difi.datahotel.util.shared.Filesystem;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FindCsvErrorsTest {

	private static String realHome;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(FindCsvErrorsTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	private File csvFile;

	@Test(expected=InvalidAttributesException.class)
	public void testFindErrorInCSVFile() throws Exception{
		
		csvFile = Filesystem.getFile("..", "csv", "ANSII_csv_ERROR.csv");

		try {
			FindCsvErrors.findErrorInCSV(csvFile);
			fail();
		} catch (InvalidAttributesException e) {
			assertEquals("It works!", "2", e.getMessage().split(":")[0]);
			throw e;
		}
	}
	
	@Test(expected=InvalidAttributesException.class)
	public void testFindErrorInCSVFile2() throws Exception {
		csvFile = Filesystem.getFile("..", "csv", "dataset_headers_UTF-8.csv");

		try {
			FindCsvErrors.findErrorInCSV(csvFile);
			fail();
		} catch (InvalidAttributesException e) {
			assertEquals("It works!", "92", e.getMessage().split(":")[0]);
			throw e;
		}
	}
	
	@Test
	public void testNoError1() throws Exception {
		csvFile = Filesystem.getFile("..", "csv", "simple-komma.csv");

		FindCsvErrors.findErrorInCSV(csvFile);
	}
	
	@Test
	public void testNoError2() throws Exception {
		csvFile = Filesystem.getFile("..", "csv", "simple-semikolon.csv");

		FindCsvErrors.findErrorInCSV(csvFile);
	}
}
