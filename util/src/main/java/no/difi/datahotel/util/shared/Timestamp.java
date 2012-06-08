package no.difi.datahotel.util.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class Timestamp {

	private static Logger logger = Logger.getLogger(Timestamp.class.getSimpleName());
	private static Gson gson = new Gson();

	private File tsfile;
	private Map<String, String> data = new HashMap<String, String>();

	public Timestamp(String... path) {
		this(Filesystem.getFile(path));
	}
	
	@SuppressWarnings("unchecked")
	public Timestamp(File tsfile) {
		this.tsfile = tsfile;

		if (tsfile.exists()) {
			try {
				FileReader reader = new FileReader(tsfile);
				data = (Map<String, String>) gson.fromJson(reader, Map.class);
				reader.close();
			} catch (Exception e) {
				logger.log(Level.WARNING, e.getMessage());
			}
		}
	}

	public void setTimestamp(long timestamp) {
		data.put("timestamp", String.valueOf(timestamp));
	}

	public long getTimestamp() {
		return data.containsKey("timestamp") ? Long.parseLong(data.get("timestamp")) : -1;
	}

	public void set(String key, String value) {
		data.put(key, value);
	}

	public void set(String key, long value) {
		set(key, String.valueOf(value));
	}

	public String get(String key) {
		return data.get(key);
	}

	public long getLong(String key) {
		return data.containsKey(key) ? Long.parseLong(data.get(key)) : 0;
	}

	public void save() {
		try {
			FileWriter writer = new FileWriter(tsfile);
			gson.toJson(data, writer);
			writer.close();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}

	@Deprecated
	public static long getTimestamp(File tsfile) {
		try {
			FileReader fReader = new FileReader(tsfile);
			BufferedReader bReader = new BufferedReader(fReader);

			long timestamp = Long.parseLong(bReader.readLine());

			bReader.close();
			fReader.close();

			return timestamp;
		} catch (Exception e) {
			return -1;
		}
	}

	@Deprecated
	public static void setTimestamp(File tsfile, long timestamp) {
		try {
			FileWriter fWriter = new FileWriter(tsfile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);

			bWriter.write(String.valueOf(timestamp));

			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			Logger.getLogger(Timestamp.class.getSimpleName()).warning(
					"Failed to write timestamp file: " + tsfile.getAbsolutePath());
		}
	}

}
