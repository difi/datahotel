package no.difi.datahotel.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * A class to handle writing of CSV files
 */
public class CSVWriter {

	private CsvWriter writer;

	/**
	 * Initializes the object and supplies the file to be written.  
	 * @param csvFile - the file to be written
	 * @throws IOException if a new exception occurred.
	 */
	public CSVWriter(File csvFile) throws IOException {
		this(csvFile.toString());
	}

	public CSVWriter(String filename) throws IOException {
		writer = new CsvWriter(filename, ';', Charset.forName("UTF-8"));
		writer.setForceQualifier(true);
		writer.setUseTextQualifier(true);
		writer.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
	}

	public CSVWriter(OutputStream stream) throws IOException {
		writer = new CsvWriter(stream, ';', Charset.forName("UTF-8"));
		writer.setForceQualifier(true);
		writer.setUseTextQualifier(true);
		writer.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
	}
	
	public void fromReader(CSVReader reader, boolean originalHeaders) throws IOException
	{
		if (originalHeaders)
			writeHeader(reader.getHeaders());
		while (reader.hasNext())
			write(reader.getNextLineArray());
	}
	
	public void writeHeader(String[] headers) throws IOException
	{
		writer.writeRecord(headers);
	}
	
	public void write(String[] values) throws IOException
	{
		writer.writeRecord(values);
	}

	public void close()
	{
		writer.close();
	}
}
