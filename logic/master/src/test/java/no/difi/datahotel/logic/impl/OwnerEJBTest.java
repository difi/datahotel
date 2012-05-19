package no.difi.datahotel.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.UserEntity;

import org.junit.Ignore;
import org.junit.Test;

public class OwnerEJBTest extends AbstractEntityManagerTest {
	private OwnerEJB ownerEJB;
	private GroupEJB groupEJB;

	private OwnerEntity owner1;
	private OwnerEntity owner2;
	private OwnerEntity owner3;
	private OwnerEntity owner4;

	private String organizationName;
	private int organizationNumber;
	private String organizationShort;
	private String organizationUrl;
	private int ownerCounter = 0;

	private GroupEntity datasetGroup1;
	private GroupEntity datasetGroup2;
	private GroupEntity datasetGroup3;

	private OwnerEJB createOwnerEJB() throws Exception {
		ownerEJB = new OwnerEJB();
		Field ownerEmField = AbstractJPAHandler.class.getDeclaredField("em");
		ownerEmField.setAccessible(true);
		ownerEmField.set(ownerEJB, em);

		groupEJB = new GroupEJB();
		Field groupEmField = AbstractJPAHandler.class.getDeclaredField("em");
		groupEmField.setAccessible(true);
		groupEmField.set(groupEJB, em);

		DatasetEJB datasetEJB = new DatasetEJB();
		Field datasetEmField = AbstractJPAHandler.class.getDeclaredField("em");
		datasetEmField.setAccessible(true);
		datasetEmField.set(datasetEJB, em);

		return ownerEJB;
	}

	private void generateTestData() {
		organizationName = "Organization Name";
		organizationNumber = 123344;
		organizationShort = "OrgName";
		organizationUrl = "www.orgUrl.com";

		owner1 = createOwnerEntity("Ola Norman", "OlaN", "www.olanor.com");
		ownerCounter++;
		owner2 = createOwnerEntity("Kari Norman", "KariN", "www.karinor.com");
		ownerCounter++;
		owner3 = createOwnerEntity("Espem Askeladd", "EspenA",
				"www.espenask.com");
		ownerCounter++;
		owner4 = createOwnerEntity(organizationName, organizationShort,
				organizationUrl);
		ownerCounter++;

		datasetGroup1 = createDatasetGroupEntity("DatasetGroup1", "DG1",
				"www.dg1.com", owner1);
		datasetGroup2 = createDatasetGroupEntity("DatasetGroup2", "DG2",
				"www.dg2.com", owner1);
		datasetGroup3 = createDatasetGroupEntity("DatasetGroup3", "DG3",
				"www.dg3.com", owner2);
	}

	@Override
	public void before() throws Exception {
		super.before();
		generateTestData();
		createOwnerEJB();
	}

	@Test
	public void getOwnerByShortName() {
		em.getTransaction().begin();
		assertEquals(owner1, ownerEJB.getOwnerByShortName("OlaN"));
		assertEquals(owner2, ownerEJB.getOwnerByShortName("KariN"));
		assertNull(ownerEJB.getOwnerByShortName("JensN"));
		em.getTransaction().commit();
	}

	@Test
	public void getAllOwners() {
		List<OwnerEntity> resultList = null;

		em.getTransaction().begin();
		resultList = ownerEJB.getAll();
		assertEquals(ownerCounter, resultList.size());
		assertTrue(resultList.contains(owner1) && resultList.contains(owner2)
				&& resultList.contains(owner3));
		em.getTransaction().commit();
	}

	@Test
	@Ignore
	public void createUser() throws Exception {
		String name = "Joakim";
		String ident = "difi";

		Method ownerCreateUser;
		ownerCreateUser = OwnerEJB.class.getDeclaredMethod("createUser",
				String.class, String.class, Integer.class, String.class,
				String.class, String.class);
		ownerCreateUser.setAccessible(true);

		em.getTransaction().begin();
		UserEntity user = (UserEntity) ownerCreateUser.invoke(ownerEJB, name,
				organizationName, organizationNumber, organizationShort,
				organizationUrl, ident);
		em.getTransaction().commit();

		assertEquals(name, user.getName());
		assertEquals(owner4, user.getOwner());
	}

	@Test
	public void getDatasetGroupByName() {
		String nullName = null;
		OwnerEntity nullOwner = null;
		em.getTransaction().begin();
		assertEquals(datasetGroup1,
				groupEJB.getDatasetGroup(datasetGroup1.getShortName(), owner1));
		assertEquals(datasetGroup2,
				groupEJB.getDatasetGroup(datasetGroup2.getShortName(), owner1));
		assertNull(groupEJB.getDatasetGroup(nullName, nullOwner));
		em.getTransaction().commit();
	}

	@Test
	public void getDatasetGroupByShortNameAndOwner() {
		em.getTransaction().begin();
		assertEquals(datasetGroup1, groupEJB.getDatasetGroup("DG1", owner1));
		assertEquals(datasetGroup2, groupEJB.getDatasetGroup("DG2", owner1));
		assertNull(groupEJB.getDatasetGroup("DG2", owner2));
		em.getTransaction().commit();
	}

	@Test
	public void getDatasetGroupsByOwner() {
		List<GroupEntity> resList = null;

		em.getTransaction().begin();
		resList = groupEJB.getByOwner(owner1);
		assertEquals(2, resList.size());
		assertTrue(resList.contains(datasetGroup1)
				&& resList.contains(datasetGroup2));

		resList = groupEJB.getByOwner(owner2);
		assertEquals(1, resList.size());
		assertTrue(resList.contains(datasetGroup3));

		resList = groupEJB.getByOwner(owner3);
		assertTrue(resList.isEmpty());
	}

	@Override
	public void after() throws Exception {
		super.after();
		ownerEJB = null;
		owner1 = null;
		owner2 = null;
		owner3 = null;

		organizationName = null;
		organizationNumber = 0;
		organizationShort = null;
		organizationUrl = null;
		ownerCounter = 0;

		datasetGroup1 = null;
		datasetGroup2 = null;
		datasetGroup3 = null;
	}
}
