package no.difi.datahotel.logic.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.DefinitionEntity;
import no.difi.datahotel.logic.model.FieldEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.ReportEntity;
import no.difi.datahotel.logic.model.ReportEntity.Status;
import no.difi.datahotel.logic.model.UserEntity;
import no.difi.datahotel.logic.model.VersionEntity;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractEntityManagerTest {

	public static EntityManagerFactory emf;
	public static EntityManager em;

	public static EntityManager setupEM(){
		try{
			emf = Persistence.createEntityManagerFactory("test");
		}catch (Exception e) {
			fail("Failed to load EntityManagerFactory: " + e.getMessage());
		}
		try{
			em = emf.createEntityManager();
		}catch (Exception e) {
			fail("Failed to load EntityManager: " + e.getMessage());
		}

		return em;
	}

	public static void closeEm(){
		if(em != null) em.close();
		if(emf != null) emf.close();

		em=null;
		emf=null;
	}

	@Before
	public void before() throws Exception{
		em = setupEM();
	}

	@After
	public void after() throws Exception{
		closeEm();
	}



	public OwnerEntity createOwnerEntity(String name, String shortName, String url){
		OwnerEntity owner = new OwnerEntity(); 
		owner.setName(name);
		owner.setShortName(shortName);
		owner.setUrl(url);


		try{
			em.getTransaction().begin();
			em.persist(owner);
			em.getTransaction().commit();
		}catch (RollbackException e) {
			fail("Failed to persist entity, " + e);
		}

		em.clear();

		assertNotNull(owner.getId());
		assertTrue("Check if primary key was created", (owner.getId() > 0));

		return owner;
	}

	public GroupEntity createDatasetGroupEntity(String name, String shortName, String url, OwnerEntity owner){
		GroupEntity datasetGroup = new GroupEntity();
		datasetGroup.setName(name);
		datasetGroup.setShortName(shortName);
		datasetGroup.setUrl(url);
		datasetGroup.setOwner(owner);


		try{
			em.getTransaction().begin();
			em.persist(datasetGroup);
			em.getTransaction().commit();
		}catch (RollbackException e) {
			fail("Failed to persist entity, " + e);
		}

		em.clear();	

		assertNotNull("Check if primary key was created", datasetGroup.getId());
		assertTrue("Check if primary key was created", datasetGroup.getId() > 0);

		return datasetGroup;
	}

	public DatasetEntity createDatasetEntity(String name, String shortName, GroupEntity datasetGroup, boolean visible){

		DatasetEntity dataset = new DatasetEntity();
		dataset.setName(name);
		dataset.setShortName(shortName);
		dataset.setDatasetGroup(datasetGroup);
		dataset.setVisible(visible);

		try{
			em.getTransaction().begin();
			em.persist(dataset);
			em.getTransaction().commit();
		}catch (RollbackException e) {
			fail("Failed to persist entity, " + e);
		}

		em.clear();	

		assertNotNull(dataset);
		assertTrue("Check if primary key was created", dataset.getId() > 0);

		return dataset;
	}

	public VersionEntity createVersionEntity(DatasetEntity dataset, long version, boolean inProgress ){
		VersionEntity versionEntity = new VersionEntity();
		versionEntity.setInProgress(inProgress);
		versionEntity.setVersion(version);
		versionEntity.setDataset(dataset);
		dataset.setCurrentVersion(versionEntity);
//		dataset.getVersions().add(versionEntity);

		try{
			em.getTransaction().begin();
			em.persist(versionEntity);
			em.merge(dataset);
			em.getTransaction().commit();
		}catch (RollbackException e) {
			fail("Failed to persist entity, " + e);
		}

		em.clear();	

		assertNotNull("Check if primary key was created", versionEntity.getId());
		assertTrue("Check if primary key was created", versionEntity.getId() > 0);

		return versionEntity;
	}

	public FieldEntity createFieldEntity(String name, String shortName, String content, 
			boolean groupable, boolean scarchable, boolean primaryIndexKey, VersionEntity version){
		FieldEntity field;
		field = new FieldEntity();
		field.setName(name);
		field.setShortName(shortName);
		field.setDescription(content);
		field.setGroupable(groupable);
		field.setSearchable(scarchable);
		field.setPrimaryIndexKey(primaryIndexKey);
		field.setVersion(version);

		try{
			em.getTransaction().begin();
			em.persist(field);
			em.getTransaction().commit();
		}catch (RollbackException e) {
			fail("Failed to persist entity, " + e);
		}

		assertNotNull(field.getId());
		assertTrue("Check if primary key was created", field.getId() > 0);

		return field;
	}

	public ReportEntity createReportEntity(String message, String email, OwnerEntity owner, 
			GroupEntity datasetGroup, DatasetEntity dataset, Status status) {
		ReportEntity report = new ReportEntity();
		report.setMessage(message);
		report.setEmail(email);
		report.setOwner(owner);
		report.setDatasetGroup(datasetGroup);
		report.setDataset(dataset);
		report.setStatus(status);

		try{
			em.getTransaction().begin();
			em.persist(report);
			em.getTransaction().commit();
		}catch (RollbackException e) {
			fail("Failed to persist entity, " + e);
		}

		assertNotNull(report.getId());
		assertTrue("Check if primary key was created", report.getId() > 0);

		return report;
	}

	public UserEntity createUserEntity(String name, OwnerEntity owner, String ident){
		UserEntity user = new UserEntity();
		user.setName(name);
		user.setOwner(owner);
		user.setIdent(ident);

		try{
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();
		}catch (RollbackException e) {
			fail("Failed to persist entity, " + e);
		}

		assertNotNull(user.getName());
		assertTrue("Check if primary key was created", user.getId() > 0);

		return user;
	}


	public DefinitionEntity createDefinitionEntity(String name, String shortName, String description){
		DefinitionEntity definition = new DefinitionEntity();
		definition.setName(name);
		definition.setShortName(shortName);
		definition.setDescription(description);

		try {
			em.getTransaction().begin();
			em.persist(definition);
			em.getTransaction().commit();
		} catch (Exception e) {
			fail("Failed to persist entity" + e);
		}

		assertNotNull(definition.getName());
		assertTrue("Check if primary key was created", definition.getId() > 0);


		return definition;
	}


}
