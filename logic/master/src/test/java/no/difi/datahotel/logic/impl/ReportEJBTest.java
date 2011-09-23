package no.difi.datahotel.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import no.difi.datahotel.logic.impl.AbstractJPAHandler;
import no.difi.datahotel.logic.impl.ReportEJB;
import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.ReportEntity;

import org.junit.Test;

public class ReportEJBTest extends AbstractEntityManagerTest {
	private ReportEJB reportEJB;
	
	private OwnerEntity owner1;
	private OwnerEntity owner2;
	private OwnerEntity owner3;
	
	private GroupEntity datasetGroup1;
	private GroupEntity datasetGroup2;
	private GroupEntity datasetGroup3;
	
	private DatasetEntity dataset1;
	private DatasetEntity dataset2;
	private DatasetEntity dataset3;
	
	private ReportEntity report1;
	private ReportEntity report2;
	private ReportEntity report3;
	private ReportEntity report4;
	
	private ReportEJB createReportEJB() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		ReportEJB reportEJB = new ReportEJB();
		Field emField = AbstractJPAHandler.class.getDeclaredField("em");
		emField.setAccessible(true);
		emField.set(reportEJB, em);
		
		return reportEJB;
	}
	
	private void generateTestData() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
		reportEJB = createReportEJB();
		
		owner1 = createOwnerEntity("Kari Nordmann", "KariN", "www.karinor.com");
		owner2 = createOwnerEntity("Per Nordmann", "PerN", "www.pernor.com");
		owner3 = createOwnerEntity("Ida Nordmann", "IdaN", "www.idanor.com");
		
		datasetGroup1 = createDatasetGroupEntity("DatasetGroup1", "DG1", "www.dg1.com", owner1);
		datasetGroup2 = createDatasetGroupEntity("DatasetGroup2", "DG2", "www.dg2.com", owner2);
		datasetGroup3 = createDatasetGroupEntity("DatasetGroup3", "DG3", "www.dg3.com", owner3);
		
		dataset1 = createDatasetEntity("Dataset1", "D1", datasetGroup2, true);
		dataset2 = createDatasetEntity("Dataset2", "D2", datasetGroup3, true);
		dataset3 = createDatasetEntity("Dataset3", "D3", datasetGroup3, true);
		
		report1 = createReportEntity("Det er en feil i datasettet", "email1@email.com", 
				owner2, datasetGroup2, dataset1, ReportEntity.Status.UNREAD);
		report2 = createReportEntity("Det er en megafeil i datasettet", "email2@email.com", 
				owner3, datasetGroup3, dataset2, ReportEntity.Status.UNREAD);
		report3 = createReportEntity("Det er en kjempefeil i datasettet", "email3@email.com", 
				owner3, datasetGroup3, dataset3, ReportEntity.Status.UNREAD);
		report4 = createReportEntity("Det er en kjempefeil i dette datasettet", "email4@email.com", 
				owner3, datasetGroup3, dataset3, ReportEntity.Status.UNREAD);
	}
	
	@Test
	public void getReportByID() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
		generateTestData();
		
		em.getTransaction().begin();
		assertEquals(report1, reportEJB.getReport(1));
		assertEquals(report2, reportEJB.getReport(2));
		em.getTransaction().commit();
	}
	
	@Test
	public void getReportsByOwner() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
		generateTestData();
		
		List<ReportEntity> resultList = null;
		
		em.getTransaction().begin();
		assertEquals(0, reportEJB.getByOwner(owner1).size());
		
		resultList = reportEJB.getByOwner(owner2);
		assertEquals(1, resultList.size());
		assertTrue(resultList.contains(report1));
		
		resultList = reportEJB.getByOwner(owner3);
		assertEquals(3, resultList.size());
		assertTrue(resultList.contains(report2) && resultList.contains(report3));
		
		em.getTransaction().commit();
	}
	
	@Test
	public void getReportsByDataset() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
		generateTestData();
		
		List<ReportEntity> resultList = null;
		
		em.getTransaction().begin();
		
		resultList = reportEJB.getByDataset(dataset1);
		assertEquals(1, reportEJB.getByDataset(dataset1).size());
		assertTrue(resultList.contains(report1));
		
		resultList = reportEJB.getByDataset(dataset2);
		assertEquals(1, reportEJB.getByDataset(dataset2).size());
		assertTrue(resultList.contains(report2));
		
		resultList = reportEJB.getByDataset(dataset3);
		assertEquals(2, reportEJB.getByDataset(dataset3).size());
		assertTrue(resultList.contains(report3) && resultList.contains(report4));
		
		em.getTransaction().commit();
	}
	
	@Test
	public void getReportsByDatasetGroups() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
		generateTestData();
		
		List<ReportEntity> resultList = null;
		
		em.getTransaction().begin();
		
		resultList = reportEJB.getByGroup(datasetGroup1);
		assertEquals(0, reportEJB.getByGroup(datasetGroup1).size());
		assertTrue(resultList.isEmpty());
		
		resultList = reportEJB.getByGroup(datasetGroup2);
		assertEquals(1, reportEJB.getByGroup(datasetGroup2).size());
		assertTrue(resultList.contains(report1));
		
		resultList = reportEJB.getByGroup(datasetGroup3);
		assertEquals(3, reportEJB.getByGroup(datasetGroup3).size());
		assertTrue(resultList.contains(report2) && resultList.contains(report3) && resultList.contains(report4));
				
		em.getTransaction().commit();
	}
	
	@Override
	public void after() throws Exception {
		super.after();
		
		reportEJB = null;
		owner1 = null;
		owner2 = null;
		owner3 = null;
		datasetGroup1 = null;
		datasetGroup2 = null;
		datasetGroup3 = null;
		dataset1 = null;
		dataset2 = null;
		dataset3 = null;
		report1 = null;
		report2 = null;
		report3 = null;
		report4 = null;
	}
	
}
