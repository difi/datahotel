package no.difi.datahotel;

import java.io.File;

import no.difi.datahotel.slave.logic.ChunkBeanTest;

import org.junit.BeforeClass;

public class BaseTest {

	@BeforeClass
	public static void beforeClass() throws Exception {
		System.setProperty("datahotel.home", new File(ChunkBeanTest.class.getResource("/").toURI()).toString() + File.separator + "datahotel");
	}
}
