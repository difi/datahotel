package no.difi.datahotel.util.csv;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CSVParser {
	
	public String[] getHeaders();
	
	public List<String[]> getTwoFirstLines() throws IOException;
	
	public Map<String, String> getNextLine() throws Exception;
	
	public String[] getNextLineArray();
	
	public char getDelimiter();

	public boolean hasNext();
	
	public void close();
	
}
