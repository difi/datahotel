package no.difi.datahotel.util.shared;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FilesystemTest {

	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(FilesystemTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	@Test
	public void testDefaultConstructor() {
		new Filesystem();
	}

	@Test
	public void testHome() {
		Assert.assertEquals(true, Filesystem.getHome().contains("datahotel"));
	}

	@Test
	public void testFolder() {
		String sep = File.separator;
		Assert.assertEquals(Filesystem.getHome().replace(File.separator + File.separator, File.separator) + "test"
				+ sep + "folder", Filesystem.getFolder("test", "folder").toString());
	}

	@Test
	public void testFile() {
		String sep = File.separator;
		Assert.assertEquals(Filesystem.getHome().replace(File.separator + File.separator, File.separator) + "test"
				+ sep + "file", Filesystem.getFile("test", "file").toString());
	}

	@Test
	public void testDeleteAdvanced() throws IOException {

		File dir1 = Filesystem.getFolder("test", "owner1", "group1", "dataset1");
		File dir2 = Filesystem.getFolder("test", "owner1", "group1", "dataset2");
		Filesystem.getFolder("test", "owner1", "group2", "dataset1");
		File file = Filesystem.getFile("test", "owner1", "group1", "dataset1", "test");

		assertTrue(file.createNewFile());

		Filesystem.delete("test", "owner1", "group1", "dataset1");
		assertFalse(dir1.exists());
		assertTrue(dir2.exists());

		Filesystem.delete("test", "owner1", "group1", "dataset2");
		assertFalse(dir2.exists());
		assertTrue(Filesystem.getFolderPath("test", "owner1").exists());

		Filesystem.delete("test", "owner1", "group2", "dataset1");
		assertFalse(Filesystem.getFolderPath("test", "owner1").exists());
	}
}
