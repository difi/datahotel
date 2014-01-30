package no.difi.datahotel;

import no.difi.datahotel.logic.ChunkBeanTest;
import org.junit.BeforeClass;

import java.io.File;

public class BaseTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.setProperty("datahotel.home", new File(ChunkBeanTest.class.getResource("/").toURI()).toString() + File.separator + "datahotel");
    }
}
