package no.difi.datahotel.util.shared;

import java.io.File;

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
}
