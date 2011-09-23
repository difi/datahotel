package no.difi.datahotel.logic.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class SettingEntityTest {
	
	@Test
	public void testConstructor() {
		SettingEntity s = new SettingEntity();
		assertNull(s.getId());
		assertNull(s.getKey());
		assertNull(s.getValue());

		s = new SettingEntity("language", "Java");
		assertNull(s.getId());
		assertEquals("language", s.getKey());
		assertEquals("Java", s.getValue());
	}

	@Test
	public void testSet() {
		SettingEntity s = new SettingEntity();
		s.setId(Long.MAX_VALUE);
		s.setKey("country");
		s.setValue("JavaZone");
		
		assertEquals(new Long(Long.MAX_VALUE), s.getId());
		assertEquals("country", s.getKey());
		assertEquals("JavaZone", s.getValue());
	}
}
