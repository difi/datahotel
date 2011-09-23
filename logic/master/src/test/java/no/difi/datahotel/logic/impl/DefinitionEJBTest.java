package no.difi.datahotel.logic.impl;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;

import no.difi.datahotel.logic.impl.AbstractJPAHandler;
import no.difi.datahotel.logic.impl.DefinitionEJB;
import no.difi.datahotel.logic.model.DefinitionEntity;

public class DefinitionEJBTest extends AbstractEntityManagerTest {
	private DefinitionEJB definitionEJB;
	
	private DefinitionEntity definition1;
	private DefinitionEntity definition2;
	private DefinitionEntity definition3;

	
	private DefinitionEJB createDefinitionEJB() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		definitionEJB = new DefinitionEJB();
		Field emField = AbstractJPAHandler.class.getDeclaredField("em");
		emField.setAccessible(true);
		emField.set(definitionEJB, em);
		
		return definitionEJB;
	}
	
	private void generateTestData(){
		definition1 = createDefinitionEntity("Definition1", "Def1", "This is a description1");
		definition2 = createDefinitionEntity("Definition2", "Def2", "This is a description2");
		definition3 = createDefinitionEntity("Definition3", "Def3", "This is a description3");
	}
	
	@Override
	public void before() throws Exception {
		super.before();
		generateTestData();
		createDefinitionEJB();
	}
	
	@Test
	public void getDefinition(){
		em.getTransaction().begin();
		assertEquals(definition1, definitionEJB.getDefinition(definition1.getId()));
		assertEquals(definition2, definitionEJB.getDefinition(definition2.getId()));
		assertNull(definitionEJB.getDefinition(5l));
		em.getTransaction().commit();
	}
	
	@Test
	public void getAllDefinitions(){
		List<DefinitionEntity> result = null;
		
		em.getTransaction().begin();
		result = definitionEJB.getAll();
		assertEquals(3, result.size());
		assertTrue(result.contains(definition1)
				&& result.contains(definition2)
				&& result.contains(definition3));
		em.getTransaction().commit();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void persist(){
		DefinitionEntity definition4 = new DefinitionEntity();
		definition4.setName("DefinitionName4");
		definition4.setShortName("DefName4");
		definition4.setDescription("This is a description4");
		
		em.getTransaction().begin();
		assertNull(definitionEJB.persist(definition1));
		em.getTransaction().commit();
		
		em.getTransaction().begin();
		assertEquals(definition4, definitionEJB.persist(definition4));
		em.getTransaction().commit();
		
	}
	

}
