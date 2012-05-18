package no.difi.datahotel.util.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Logger;

public class Timestamp {

	public static long getTimestamp(File tsfile) {
		try {
			FileReader fReader = new FileReader(tsfile);
			BufferedReader bReader = new BufferedReader(fReader);
			
			long timestamp = Long.parseLong(bReader.readLine());
			
			bReader.close();
			fReader.close();

			return timestamp;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static void setTimestamp(File tsfile, long timestamp) {
		try {
			FileWriter fWriter = new FileWriter(tsfile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			
			bWriter.write(String.valueOf(timestamp));
			
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			Logger.getLogger(Timestamp.class.getSimpleName()).warning("Failed to write timestamp file: " + tsfile.getAbsolutePath());
		}
	}
	
}
