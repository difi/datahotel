package no.difi.datahotel.util;

import java.io.File;

import no.difi.datahotel.BaseTest;

import org.junit.Assert;
import org.junit.Test;

public class FilesystemTest extends BaseTest {

	@Test
	public void testDefaultConstructor() {
		new Filesystem();
	}

	@Test
	public void testHome() {
		Assert.assertTrue(Filesystem.getHome().contains("datahotel"));
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
