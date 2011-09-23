package no.difi.datahotel.logic.impl;

import java.lang.reflect.Field;

import no.difi.datahotel.logic.impl.AbstractJPAHandler;
import no.difi.datahotel.logic.impl.SettingEJB;

import org.junit.Assert;
import org.junit.Test;

public class SettingEJBTest extends AbstractEntityManagerTest {

	private SettingEJB settingEjb;
	
	@Override
	public void before() throws Exception {
		super.before();
		
		settingEjb = new SettingEJB();
		Field emField = AbstractJPAHandler.class.getDeclaredField("em");
		emField.setAccessible(true);
		emField.set(settingEjb, em);
	}

	@Test
	public void testing(){
		
		em.getTransaction().begin();
		settingEjb.set("hello", "world");
		em.getTransaction().commit();
		
		Assert.assertEquals("world",  settingEjb.get("hello"));
		Assert.assertEquals(1, settingEjb.getAll().size());
		Assert.assertEquals("world", settingEjb.getAll().get("hello"));
		
		em.getTransaction().begin();
		settingEjb.set("hello", "Mathilde");
		em.getTransaction().commit();
		
		Assert.assertEquals("Mathilde", settingEjb.get("hello"));
		Assert.assertEquals(1, settingEjb.getAll().size());
		
		em.getTransaction().begin();
		settingEjb.set("codeking", "Even");
		em.getTransaction().commit();
		
		Assert.assertEquals("Even", settingEjb.get("codeking"));
		Assert.assertEquals("Even", settingEjb.get("codeking", "Erlend"));
		Assert.assertEquals(2, settingEjb.getAll().size());
		
		Assert.assertEquals(null, settingEjb.get("Erlend"));
		Assert.assertEquals(2, settingEjb.getAll().size());
		
		em.getTransaction().begin();
		settingEjb.set("codeking", null);
		em.getTransaction().commit();
		
		Assert.assertNull(settingEjb.get("codeking"));
		Assert.assertEquals("Even", settingEjb.get("codeking", "Even"));

		em.getTransaction().begin();
		settingEjb.set("codeking", "");
		em.getTransaction().commit();

		Assert.assertNull(settingEjb.get("codeking"));
	}

        @Test
        public void nonExistingKeyGivesNull()
        {
            Assert.assertNull(settingEjb.get("gibberish"));
        }
}
