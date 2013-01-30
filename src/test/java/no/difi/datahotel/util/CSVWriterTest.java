package no.difi.datahotel.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;

import no.difi.datahotel.BaseTest;

import org.junit.Test;

public class CSVWriterTest extends BaseTest {

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

		File fileR = new File(this.getClass().getResource("/csv/simple-komma.csv").getFile());
		CSVReader parser = new CSVReader(fileR);
		
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
