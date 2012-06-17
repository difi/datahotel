package no.difi.datahotel.util.shared;

import java.io.File;

public class Filesystem {

	private static final String HOME = "datahotel";

	public static final String FOLDER_MASTER = "master";
	public static final String FOLDER_MASTER_DEFINITION = FOLDER_MASTER + File.separator + "definition";
	public static final String FOLDER_SLAVE = "slave";
	public static final String FOLDER_CACHE = "cache";
	public static final String FOLDER_CACHE_CHUNK = FOLDER_CACHE + File.separator + "chunk";
	public static final String FOLDER_CACHE_INDEX = FOLDER_CACHE + File.separator + "index";

	public static final String FILE_DATASET = "dataset.csv";
	public static final String FILE_DATASET_ORIGINAL = "original.csv";
	public static final String FILE_FIELDS = "fields.xml";
	public static final String FILE_DEFINITIONS = "definitions.xml";
	public static final String FILE_METADATA = "meta.xml";
	public static final String FILE_VERSION = "version.xml";

	public static String getHome() {
		String dir = System.getProperty("user.home");
		dir = String.valueOf(dir + File.separator).replace(File.separator, File.separator + File.separator);
		dir += HOME + File.separator;
		new File(dir).mkdirs();
		return dir;
	}

	public static File getFolderPath(String... folder) {
		String dir = getHome();
		for (String f : folder)
			if (!"".equals(f))
				dir += f.replace("/", File.separator) + File.separator;
		return new File(dir);
	}

	public static File getFolder(String... folder) {
		File dir = getFolderPath(folder);

		if (!dir.exists())
			dir.mkdirs();

		return dir;
	}

	public static File getFile(String... uri) {
		String[] dir = new String[uri.length - 1];
		for (int i = 0; i < uri.length - 1; i++)
			dir[i] = uri[i];

		return new File(getFolder(dir).toString() + File.separator + uri[uri.length - 1]);
	}

	public static File getFile(File folder, String... uri) {
		String path = folder.toString();
		for (int i = 0; i < uri.length - 1; i++)
			path += uri[i].replace("/", File.separator) + File.separator;

		return new File(path + File.separator + uri[uri.length - 1]);
	}

	public static void delete(String folder, String location) {
		delete(getFolder(folder, location));
	}

	public static void delete(String folder) {
		delete(getFolder(folder));
	}

	public static void delete(File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory())
				delete(f);
			else
				f.delete();
		}
		folder.delete();
	}
}
