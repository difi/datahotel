package no.difi.datahotel.logic.impl;

import java.lang.reflect.Field;

import no.difi.datahotel.logic.impl.AbstractJPAHandler;
import no.difi.datahotel.logic.impl.LoginEJB;
import no.difi.datahotel.logic.impl.SettingEJB;

import org.junit.Ignore;
import org.junit.Test;
import org.scribe.model.Token;

public class LoginEJBTest extends AbstractEntityManagerTest{
	
	private SettingEJB settings;
	private LoginEJB loginEJB;
	
	@Override
	public void before() throws Exception {
		super.before();
		
		settings = new SettingEJB();
		Field settingsEmField = AbstractJPAHandler.class.getDeclaredField("em");
		settingsEmField.setAccessible(true);
		settingsEmField.set(settings, em);
		
		loginEJB = new LoginEJB();
		Field settingsField = LoginEJB.class.getDeclaredField("settingEJB");
		settingsField.setAccessible(true);
		settingsField.set(loginEJB, settings);		
	}
	
	@Test
	@Ignore
	public void _getUser()  {	
		Token accessToken = new Token("0326fffa-c29a-40a7-b438-d468a13b44db", "adad4bbe-e4b6-4171-bd63-e58850c58fca");

		em.getTransaction().begin();
		loginEJB._getUser(accessToken);
		em.getTransaction().commit();
	}
}
