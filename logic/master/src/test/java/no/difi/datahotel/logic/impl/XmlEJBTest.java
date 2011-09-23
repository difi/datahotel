package no.difi.datahotel.logic.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;

import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.VersionEntity;
import no.difi.datahotel.util.bridge.Structure;
import no.difi.datahotel.util.shared.Filesystem;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

public class XmlEJBTest extends AbstractEntityManagerTest {
	OwnerEJB ownerEJB;
	DatasetEJB datasetEJB;
	XmlEJB xmlEJB;

	private static  String FILE_PATH = System.getProperty("java.io.tmpdir");
	{
		if ( !(FILE_PATH.endsWith("/") || FILE_PATH.endsWith("\\")) )
			FILE_PATH += System.getProperty("file.separator");

		FILE_PATH += "XmlEJBTest.xml";
	}

	public void before() throws Exception {
		super.before();
		
		xmlEJB = new XmlEJB();
		ownerEJB = new OwnerEJB();
		
		Field ownerEmField = AbstractJPAHandler.class.getDeclaredField("em");
		ownerEmField.setAccessible(true);
		ownerEmField.set(ownerEJB, em);
		
		Field xmlOwnerField = XmlEJB.class.getDeclaredField("ownerEJB");
		xmlOwnerField.setAccessible(true);
		xmlOwnerField.set(xmlEJB, ownerEJB);
	}

	@SuppressWarnings("unused")
	@Ignore
	@Test
	public void saveDatasetStructureToDisk() throws Exception {

		//populateDatasetStructure
		OwnerEntity owner = createOwnerEntity("owner", "o", null);

		GroupEntity datasetGroup1 = createDatasetGroupEntity("datasetGroup1", "dg1", null, owner);
		GroupEntity datasetGroup2 = createDatasetGroupEntity("datasetGroup2", "dg2", null, owner);

		DatasetEntity dataset1 = createDatasetEntity("dataset1", "d1", datasetGroup1, true);
		DatasetEntity dataset2 = createDatasetEntity("dataset2", "d2", datasetGroup1, true);
		DatasetEntity dataset3 = createDatasetEntity("dataset3", "d3", datasetGroup2, true);
		DatasetEntity dataset4 = createDatasetEntity("dataset4", "d4", datasetGroup2, true);

		VersionEntity version1 = createVersionEntity(dataset1, 1l, true);
		VersionEntity version2 = createVersionEntity(dataset2, 2l, true);
		VersionEntity version3 = createVersionEntity(dataset3, 3l, true);
		VersionEntity version4 = createVersionEntity(dataset4, 4l, true);

		Structure datasetStructure = new Structure();
		{
			List<OwnerEntity> ownerList = new ArrayList<OwnerEntity>();
			ownerList.add(owner);

			em.setFlushMode(FlushModeType.COMMIT);
			em.getTransaction().begin();
			
			em.find(OwnerEntity.class, 1l);
			em.find(GroupEntity.class, 1l);
			em.find(GroupEntity.class, 2l);
			em.find(DatasetEntity.class, 1l);
			em.find(DatasetEntity.class, 2l);
			em.find(DatasetEntity.class, 3l);
			em.find(DatasetEntity.class, 4l);
			em.find(VersionEntity.class, 1l);
			em.find(VersionEntity.class, 2l);
			em.find(VersionEntity.class, 3l);
			em.find(VersionEntity.class, 4l);
			
			
			Method populateDataset = XmlEJB.class.getDeclaredMethod("populateDatasetStructure", Structure.class, List.class);
			populateDataset.setAccessible(true);

			populateDataset.invoke(xmlEJB, datasetStructure, ownerList);

			em.getTransaction().commit();

			assertEquals(1, datasetStructure.getStructure().size());
			assertEquals(owner.getShortName(), datasetStructure.getStructure().keySet().iterator().next());

			Map<String, Map<String, Long>>  owners = datasetStructure.getStructure().values().iterator().next();

			assertEquals(2, owners.keySet().size());

			assertTrue(owners.keySet().contains(datasetGroup1.getShortName())
					&& owners.keySet().contains(datasetGroup2.getShortName()));

			Map<String, Long> datasetgroups = owners.values().iterator().next();

			assertEquals(2, datasetgroups.keySet().size());

			assertTrue(datasetgroups.keySet().contains(dataset3.getShortName())
					&& datasetgroups.keySet().contains(dataset4.getShortName()));
		}
		//saveXML
		{


			Method saveMethod = XmlEJB.class.getDeclaredMethod("saveXML", String.class, Object.class);
			saveMethod.setAccessible(true);
			saveMethod.invoke(xmlEJB, FILE_PATH, datasetStructure);


			assertTrue(new File(FILE_PATH).exists());
		}
		//readDatasetStructure()
		{
			Structure readDatasetStructure = null;
			FileUtils.copyFile(new File(FILE_PATH), Filesystem.getFileF(Filesystem.FOLDER_SHARED, Filesystem.STRUCTURE));
			readDatasetStructure = Structure.read();

			assertEquals(1, readDatasetStructure.getStructure().size());
			assertEquals(owner.getShortName(), readDatasetStructure.getStructure().keySet().iterator().next());

			Map<String, Map<String, Long>> datasetGroups = readDatasetStructure.getStructure().values().iterator().next();

			assertEquals(2, datasetGroups.keySet().size());

			assertTrue(datasetGroups.keySet().contains(datasetGroup1.getShortName())
					&& datasetGroups.keySet().contains(datasetGroup2.getShortName()));

			Map<String, Long> datasets = datasetGroups.values().iterator().next();

			assertEquals(2, datasets.keySet().size());

			assertTrue(datasets.keySet().contains(dataset3.getShortName())
					&& datasets.keySet().contains(dataset3.getShortName()));	
		}

	}

}
