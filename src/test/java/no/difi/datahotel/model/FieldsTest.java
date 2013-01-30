package no.difi.datahotel.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import no.difi.datahotel.BaseTest;
import no.difi.datahotel.util.Filesystem;

import org.junit.Test;

public class FieldsTest extends BaseTest {

	@Test
	public void testSaveRead() throws Exception {

		Fields f = new Fields();
		
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("id", false));
		fields.add(new Field("name", true));
		f.setFields(fields);
		
		f.save("difi", "test", "people");
		assertTrue(Filesystem.getFile(Filesystem.FOLDER_SLAVE, "difi", "test", "people", Filesystem.FILE_FIELDS).exists());
		
		Fields f2 = Fields.read("difi/test/people");
		assertEquals(fields.size(), f2.getFields().size());
		for (int i = 0; i < fields.size(); i++)
			assertEquals(fields.get(i), f2.getFields().get(i));
			
	}
}
