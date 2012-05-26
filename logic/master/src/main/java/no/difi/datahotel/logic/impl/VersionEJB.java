package no.difi.datahotel.logic.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.FieldEntity;
import no.difi.datahotel.logic.model.VersionEntity;
import no.difi.datahotel.util.csv.CSVParser;
import no.difi.datahotel.util.csv.CSVParserFactory;
import no.difi.datahotel.util.csv.CSVWriter;
import no.difi.datahotel.util.shared.Filesystem;

@Stateless
public class VersionEJB extends AbstractJPAHandler {

	@EJB
	private FieldEJB fieldEJB;
	
	public VersionEntity create(InputStream inputStream, DatasetEntity dataset) throws Exception {
		VersionEntity version = new VersionEntity();
		version.setDataset(dataset);
		version.setInProgress(true);
		version.setVersion(new Date().getTime());
		save(version);

		File filename = Filesystem.getFile(Filesystem.FOLDER_MASTER, String.valueOf(dataset.getId()),
				String.valueOf(version.getVersion()) + "-original.csv");

		// http://stackoverflow.com/questions/1477269/write-a-binary-downloaded-file-to-disk-in-java
		byte[] buffer = new byte[8 * 1024];

		try {
			OutputStream output = new FileOutputStream(filename);
			try {
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					output.write(buffer, 0, bytesRead);
				}
			} finally {
				output.close();
			}
		} finally {
			inputStream.close();
		}

		return version;
	}

	public VersionEntity getByDatasetAndTimestamp(DatasetEntity dataset, Long timestamp) {
		try {
			Query query = em.createNamedQuery(VersionEntity.BY_DATASET_AND_TIMESTAMP);
			query.setParameter("dataset", dataset);
			query.setParameter("timestamp", timestamp);

			return (VersionEntity) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<VersionEntity> getByDataset(DatasetEntity dataset) {
		Query query = em.createNamedQuery(VersionEntity.BY_DATASET);
		query.setParameter("dataset", dataset);

		return query.getResultList();
	}

	/**
	 * Returns the two first lines of the CSV-file of the version as read by the CSV-parser.
	 * @param version
	 * @return
	 * @throws Exception
	 */
	public List<String[]> getTop(VersionEntity version) throws Exception {
		File filename = Filesystem.getFile(Filesystem.FOLDER_MASTER, String.valueOf(version.getDataset().getId()),
				String.valueOf(version.getVersion()) + "-original.csv");

		CSVParser parser = CSVParserFactory.getCSVParserSmart(filename);
		parser.close();

		return parser.getTwoFirstLines();
	}

	public void saveFile(VersionEntity version) throws Exception {
		File original = Filesystem.getFile(Filesystem.FOLDER_MASTER, String.valueOf(version.getDataset().getId()),
				String.valueOf(version.getVersion()) + "-original.csv");
		File target = Filesystem.getFile(Filesystem.FOLDER_SLAVE, version.getDataset().getDatasetGroup().getOwner()
				.getShortName(), version.getDataset().getDatasetGroup().getShortName(), version.getDataset()
				.getShortName(), Filesystem.FILE_DATASET);

		CSVParser parser = CSVParserFactory.getCSVParserSmart(original);
		CSVWriter writer = new CSVWriter(target);
		
		List<FieldEntity> fields = fieldEJB.getByVersion(version);
		String[] header = new String[fields.size()];
		for (int i = 0; i < fields.size(); i++)
			header[i] = fields.get(i).getShortName();
		
		writer.writeHeader(header);
		writer.fromReader(parser, false);
		writer.close();

		parser.close();

	}
}
