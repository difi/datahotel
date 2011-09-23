package no.difi.datahotel.util.shared;

import java.io.File;

public class Filesystem {
	
	public static final String STRUCTURE = "datasetStructure.xml";
	public static final String OWNER_METADATA = "owner.xml";
	public static final String GROUP_METADATA = "datasetGroup.xml";
	public static final String DATASET_DATA = "dataset.csv";
	public static final String DATASET_FIELDS = "fields.xml";
	public static final String DATASET_METADATA = "metadata.xml";
	
	private static final String FOLDER_DATAHOTEL = ".datahotel";

	public static final String FOLDER_ARCHIVE = "archive";
	public static final String FOLDER_CHUNK = "chunk";
	public static final String FOLDER_INDEX = "index";
	public static final String FOLDER_SHARED = "import";
	
	public static String getHome() {
		String dir = System.getProperty("user.home");
		dir = String.valueOf(dir + File.separator).replace(File.separator, File.separator + File.separator);
		dir += FOLDER_DATAHOTEL + File.separator;
		new File(dir).mkdirs();
		return dir;
	}

	public static File getFolderPathF(String... folder) {
		String dir = getHome();
		for (String f : folder)
			dir += f + File.separator;
		return new File(dir);
	}
	
	public static File getFolderF(String... folder) {
		File dir = getFolderPathF(folder);

		if(!dir.exists())
			dir.mkdirs();

		return dir;
	}
	
	public static File getFileF(String... uri) {
		String[] dir = new String[uri.length - 1];
		for (int i = 0; i < uri.length - 1; i++)
			dir[i] = uri[i];
		
		return new File(getFolderF(dir).toString() + File.separator + uri[uri.length - 1]);
	}
	
	public static void delete(String folder, String owner, String group, String dataset) {
		File target = Filesystem.getFolderF(folder, owner, group, dataset);
		for (File f : target.listFiles())
			f.delete();
		target.delete();

		File parentGroup = new File(target.getParent());
		if (parentGroup.listFiles().length == 0) {
			parentGroup.delete();
			File parentOwner = new File(parentGroup.getParent());
			if (parentOwner.listFiles().length == 0)
				parentOwner.delete();
		}
	}
}
