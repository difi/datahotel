package no.difi.datahotel.util.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import com.glaforge.i18n.io.SmartEncodingInputStream;

public class FindCsvErrors {

	private static final String[] possibleDelimiters = {",",";",":","\t"};

	public static void findErrorInCSV(File csvFile) throws InvalidAttributesException, IOException {
		String[] tempString;
		String nextLine;
		Charset charset;
		InputStreamReader inReader = null;
		BufferedReader bufReader = null;
		List<Integer> wordCounter = new ArrayList<Integer>();
		InputStream inStream = new FileInputStream(csvFile);
		try {
			charset = findCharset(inStream);
			inStream.close();
			inStream = new FileInputStream(csvFile);
			inReader = new InputStreamReader(inStream, charset);
			bufReader = new BufferedReader(inReader);
			String delimiter = findDelimiter(bufReader);
			bufReader.close();
			inReader.close();
			inStream.close();
			inStream = new FileInputStream(csvFile);
			inReader = new InputStreamReader(inStream, charset);
			bufReader = new BufferedReader(inReader);
			
			while((nextLine = bufReader.readLine()) != null) {
				tempString = nextLine.split(delimiter);
				wordCounter.add(tempString.length);
			}
		} finally {
			bufReader.close();
			inReader.close();
			inStream.close();
		}

		int highestCount = 0;
		int lowestCount = Integer.MAX_VALUE;
		int rowCounter = 0;
		int lineWithFault = 0;
		for (Integer integer : wordCounter) {
			if(highestCount < integer) {
				highestCount = integer;
				lineWithFault = rowCounter;
			}
			if(lowestCount > integer) {
				lowestCount = integer;
				lineWithFault = rowCounter;
			}
			rowCounter++;
		}
		if (highestCount != lowestCount) {
			throw new InvalidAttributesException((lineWithFault +1) + ":This line has an error, it is either missing a field or has one too many");
		}
	}
	/**
	 * Method that finds the delimiter in a csv file.
	 * @param csvFile
	 * @return The delimiter as a char
	 * @throws IOException 
	 */
	public static String findDelimiter(Reader reader) throws IOException {
		BufferedReader bufReader = new BufferedReader(reader);
		bufReader.mark(1048576);
		int[] counter = new int[4];
		int result = 0;
		int position = 0;
		String[] counterString = null;

		for (int i = 0; i < possibleDelimiters.length; i++) {
			counterString = bufReader.readLine().split(possibleDelimiters[i]);
			counter[i] = counterString.length;
			bufReader.reset();
		}
		for (int i = 0; i < counter.length; i++) {
			if(counter[i] > result) {
				result = counter[i];
				position = i;
			}
		}
		bufReader.close();
		reader.close();
		return possibleDelimiters[position];
	}

	public static Charset findCharset(InputStream inStream) throws IOException {
		SmartEncodingInputStream smartIS = new SmartEncodingInputStream(inStream);
		Charset charset = smartIS.getEncoding();
		smartIS.close();
		return charset;
	}
}
