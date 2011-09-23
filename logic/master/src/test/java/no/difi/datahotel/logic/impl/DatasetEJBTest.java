package no.difi.datahotel.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.VersionEntity;

import org.junit.Ignore;
import org.junit.Test;

public class DatasetEJBTest extends AbstractEntityManagerTest {
	private DatasetEJB datasetEJB;

	private OwnerEntity owner1;

	private GroupEntity datasetGroup1;
	private GroupEntity datasetGroup2;

	private DatasetEntity dataset1;
	private DatasetEntity dataset2;
	private DatasetEntity dataset3;
	private DatasetEntity dataset4;
	private DatasetEntity dataset5;
	private DatasetEntity dataset6;
	private DatasetEntity dataset7;
	
	private VersionEntity version1;
	// private VersionEntity version2;
	private VersionEntity version3;
//	private VersionEntity version4;
//	private VersionEntity version5;
//	private VersionEntity version6;
//	private VersionEntity version7;
//	private VersionEntity version8;
//	private VersionEntity version9;
	

	private DatasetEJB createDatasetEJB() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		datasetEJB = new DatasetEJB();
		Field emField = AbstractJPAHandler.class.getDeclaredField("em");
		emField.setAccessible(true);
		emField.set(datasetEJB, em);
		
		return datasetEJB;
	}

	private void generateTestData(){
		owner1 = createOwnerEntity("Ola Norman", "OlaN", "www.olanor.com");
		
		datasetGroup1 = createDatasetGroupEntity("DatasetGroup1", "DG1", "www.dg1.com", owner1);
		datasetGroup2 = createDatasetGroupEntity("DatasetGroup2", "DG2", "www.dg2.com", owner1);

		dataset1 = createDatasetEntity("DatasetName1", "DataName1", datasetGroup1, true);
		dataset2 = createDatasetEntity("DatasetName2", "DataName2", datasetGroup1, true);
		dataset3 = createDatasetEntity("DatasetName3", "DataName3", datasetGroup2, true);
		dataset4 = createDatasetEntity("DatasetName4", "DataName4", datasetGroup2, true);
		dataset5 = createDatasetEntity("DatasetName5", "DataName5", datasetGroup2, true);
		dataset6 = createDatasetEntity("DatasetName6", "DataName6", datasetGroup2, true);
		dataset7 = createDatasetEntity("DatasetName7", "DataName7", datasetGroup2, true);

		
		version1 = createVersionEntity(dataset1, 1l, false);
		createVersionEntity(dataset1, 2l, false);
		version3 = createVersionEntity(dataset2, 3l, false);
//		version4 = createVersionEntity(dataset3, 4l, false);
//		version5 = createVersionEntity(dataset4, 5l, false);
//		version6 = createVersionEntity(dataset5, 6l, false);
//		version7 = createVersionEntity(dataset6, 7l, true);
//		version8 = createVersionEntity(dataset7, 8l, false);
//		version9 = createVersionEntity(dataset1, 9l, true);
		
		createFieldEntity("FieldName1", "FN1", "Biler", true, true, true, version1);
		createFieldEntity("FieldName2", "FN1", "Traktor", true, true, true, version3);

	}
	
	@Override
	public void before() throws Exception {
		super.before();
		generateTestData();
		createDatasetEJB();
	}
	

	@Test
	public void getDatasetByShortNameDatasetGroup(){
		em.getTransaction().begin();
		assertEquals(dataset1, 
				datasetEJB.getByShortNameAndGroup("DataName1", datasetGroup1));
		assertEquals(dataset2, 
				datasetEJB.getByShortNameAndGroup("DataName2", datasetGroup1));
		assertNull(datasetEJB.getByShortNameAndGroup("DataTull", datasetGroup1));
		em.getTransaction().commit();
	}
	
	@Test
	public void getAllDatasetEntities(){
		List<DatasetEntity> result = null;
		
		em.getTransaction().begin();
		result = datasetEJB.getAll();
		assertEquals(7, result.size());
		assertTrue(result.contains(dataset1) 
				&& result.contains(dataset2) 
				&& result.contains(dataset3)
				&& result.contains(dataset4) 
				&& result.contains(dataset5) 
				&& result.contains(dataset6)
				&& result.contains(dataset7));
		em.getTransaction().commit();
	}
	
	@Test
	@Ignore
	public void getFiveLastDatasets(){
		List<DatasetEntity> result = null;
		
		em.getTransaction().begin();
		result = datasetEJB.getFiveLastDatasets();
		assertEquals(5, result.size());
		assertTrue(result.contains(dataset7) 
				&& result.contains(dataset6)
				&& result.contains(dataset5)
				&& result.contains(dataset4)
				&& result.contains(dataset1));
		em.getTransaction().commit();
	}
	
	@Override
	public void after() throws Exception {
		super.after();
		datasetEJB = null;

		owner1 = null;

		datasetGroup1 = null;
		datasetGroup2 = null;

		dataset1 = null;
		dataset2 = null;
		dataset3 = null;
		dataset4 = null;
		dataset5 = null;
		dataset6 = null;
		dataset7 = null;
		
		version1 = null;
		version3 = null;
	}

}
