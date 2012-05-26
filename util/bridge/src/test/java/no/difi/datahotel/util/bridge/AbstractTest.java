package no.difi.datahotel.util.bridge;

import java.io.File;

import no.difi.datahotel.util.shared.Filesystem;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractTest {

	private static String realHome;

	@BeforeClass
	public static void beforeClass() throws Exception {
		realHome = System.getProperty("user.home");
		System.setProperty("user.home", new File(DefinitionTest.class.getResource("/").toURI()).toString());
	}

	@AfterClass
	public static void afterClass() {
		System.setProperty("user.home", realHome);
	}

	@Test(expected = Exception.class)
	public void testSaveFail() throws Exception {
		new AbstractClass().save();
	}

	@Test
	public void testSaveRead() {
		Assert.assertNull(new AbstractClass().read());
	}

	public class AbstractClass extends Abstract {
		public void save() throws Exception {
			super.save(Filesystem.getFile("failSave.xml"), null);
		}

		public AbstractClass read() {
			return (AbstractClass) read(AbstractClass.class, Filesystem.getFile("failRead.xml"));
		}
	}

}
