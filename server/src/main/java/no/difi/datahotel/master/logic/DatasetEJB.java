package no.difi.datahotel.master.logic;

import static no.difi.datahotel.util.Filesystem.FILE_DATASET;
import static no.difi.datahotel.util.Filesystem.FILE_DATASET_ORIGINAL;
import static no.difi.datahotel.util.Filesystem.FILE_VERSION;
import static no.difi.datahotel.util.Filesystem.FOLDER_MASTER;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.Version;
import no.difi.datahotel.util.Disk;
import no.difi.datahotel.util.Filesystem;

import org.apache.commons.io.IOUtils;

@Stateless
public class DatasetEJB {

	public Version addVersion(Metadata metadata, InputStream inputStream) {
		try {
			Version version = new Version();
			version.setTimestamp(System.currentTimeMillis());

			FileReader reader;
			FileWriter writer;

			File folder = Filesystem.getFolder(FOLDER_MASTER, metadata.getLocation(),
					String.valueOf(version.getTimestamp()));
			File target = Filesystem.getFile(folder, FILE_DATASET_ORIGINAL);
			writer = new FileWriter(target);
			IOUtils.copy(inputStream, writer);
			writer.close();

			// TODO Fix file here...
			reader = new FileReader(target);
			writer = new FileWriter(Filesystem.getFile(folder, FILE_DATASET));
			IOUtils.copy(reader, writer);
			reader.close();
			writer.close();

			Disk.save(Filesystem.getFile(folder, FILE_VERSION), version);
			metadata.getLogger().info("Uploaded file.");

			return version;
		} catch (Exception e) {
			return null;
		}
	}

	public List<Version> getVersions(Metadata metadata) {
		File home = Filesystem.getFolder(FOLDER_MASTER, metadata.getLocation());
		List<Version> versions = new ArrayList<Version>();
		for (File f : home.listFiles())
			if (f.isDirectory() && Filesystem.getFile(f, FILE_VERSION).exists())
				versions.add((Version) Disk.read(Version.class, Filesystem.getFile(f, FILE_VERSION)));
		return versions;
	}

	public Version getVersion(Metadata metadata, String timestamp) {
		File folder = Filesystem.getFolderPath(FOLDER_MASTER, metadata.getLocation(), timestamp);
		if (folder.isDirectory())
			return (Version) Disk.read(Version.class, Filesystem.getFile(folder, FILE_VERSION));
		return null;
	}

	public void publishVersion(Metadata metadata, String timestamp) {
		Version version = getVersion(metadata, timestamp);

		try {
			if (version != null) {
				metadata.getLogger().info("Publishing version " + version.getTimestamp());

				FileReader reader = new FileReader(Filesystem.getFile(FOLDER_MASTER, metadata.getLocation(), timestamp,
						FILE_DATASET));
				FileWriter writer = new FileWriter(Filesystem.getFile(FOLDER_SLAVE, metadata.getLocation(),
						FILE_DATASET));
				IOUtils.copy(reader, writer);
				reader.close();
				writer.close();

				// TODO Write fields.

				metadata.setUpdated(System.currentTimeMillis());
				metadata.setVersion(version.getTimestamp());
				metadata.save();

				metadata.getLogger().info("Published: " + version.getTimestamp());
			}
		} catch (Exception e) {

		}
	}
}
