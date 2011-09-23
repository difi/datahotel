package no.difi.datahotel.client.wizard;


import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.junit.Test;

import junit.framework.TestCase;

public class UploadBeanTest extends TestCase {

	private UploadedFile uploadedFile;
	
	public void initTest() {
		URL url = this.getClass().getResource("/dataset_headers.csv");
		File csvFile = new File(url.getFile());

		uploadedFile = new UploadedFilePlaceholder(csvFile);
	}
	
	@Test
	public void testInputStream() {
		initTest();
		try { 
			assertNotNull(uploadedFile.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFileName() {
		initTest();
		assertNotNull(FilenameUtils.getName(uploadedFile.getName()));
	}
	
}
