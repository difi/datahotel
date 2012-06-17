package no.difi.datahotel.util.csv;

import java.io.ByteArrayOutputStream;
import java.io.File;

import no.difi.datahotel.util.Filesystem;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class CSVWriterTest {

	private static String realHome;
	
	@BeforeClass
	public static void beforeClass() {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", System.getProperty("user.dir") + File.separator + "src" + File.separator
				+ "test" + File.separator + "resources" + File.separator + "csv");
	}
	
	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}
	
	@Test
	public void testSimple() throws Exception {
		File file = Filesystem.getFile("testing1.csv");

		CSVWriter writer = new CSVWriter(file);
		writer.writeHeader(new String[] { "id", "name", "age" });
		writer.write(new String[] { "1", "Ole", "2" });
		writer.close();
	}

	@Test
	public void testReadAndWrite() throws Exception {
		File file = Filesystem.getFile("testing2.csv");
		System.out.println(file);

		CSVMetainfo metainfo = new CSVMetainfo(',', true, true);
		File fileR = new File(this.getClass().getResource("/csv/simple-komma.csv").getFile());
		CSVParser parser = new CSVParserImpl(fileR, metainfo);
		
		CSVWriter writer = new CSVWriter(file);
		writer.fromReader(parser, true);
		writer.close();
	}
	
	@Test
	public void testReadAndWriteSmart1() throws Exception {
		File file = Filesystem.getFile("testing3.csv");

		File fileR = new File(this.getClass().getResource("/csv/simple-smart1.csv").getFile());
		CSVParser parser = CSVParserFactory.getCSVParserSmart(fileR);
		
		CSVWriter writer = new CSVWriter(file);
		writer.fromReader(parser, true);
		writer.close();
	}

	@Test
	public void testReadAndWriteSmart2() throws Exception {
		File file = Filesystem.getFile("testing4.csv");

		File fileR = new File(this.getClass().getResource("/csv/simple-smart2.csv").getFile());
		CSVParser parser = CSVParserFactory.getCSVParserSmart(fileR);
		
		CSVWriter writer = new CSVWriter(file);
		writer.fromReader(parser, true);
		writer.close();
	}
	
	@Test
	public void testWriteToOutputStream() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CSVWriter writer = new CSVWriter(baos);

		writer.writeHeader(new String[] { "id", "name", "age" });
		writer.write(new String[] { "1", "Ole", "2" });
		writer.close();

		String result = baos.toString("UTF-8");
		assertEquals(5, result.split(";").length);
		assertEquals(2, result.split("\n").length);
	}
}
