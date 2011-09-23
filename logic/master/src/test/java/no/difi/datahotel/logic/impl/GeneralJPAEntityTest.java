package no.difi.datahotel.logic.impl;

import static no.difi.datahotel.logic.model.DatasetEntity.BY_GROUP;
import static no.difi.datahotel.logic.model.DatasetEntity.NQ_GET_ALL_DATASET;
import static no.difi.datahotel.logic.model.GroupEntity.ALL;
import static no.difi.datahotel.logic.model.GroupEntity.BY_OWNER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.FieldEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.ReportEntity;
import no.difi.datahotel.logic.model.ReportEntity.Status;

import org.junit.Test;

public class GeneralJPAEntityTest extends AbstractEntityManagerTest {
	@SuppressWarnings("unchecked")
	@Test
	public void namedQuery_DatasetEntity(){
		
		OwnerEntity owner = createOwnerEntity("owner 1", "o1", null);
		
		GroupEntity datasetGroup1 = createDatasetGroupEntity("datasetGroup 1", "dg1", null, owner);
		GroupEntity datasetGroup2 = createDatasetGroupEntity("datasetGroup 2", "dg2", null, owner);
		
		DatasetEntity dataset1 = createDatasetEntity("dataset 1", "d1", datasetGroup1, true);
		DatasetEntity dataset2 = createDatasetEntity("dataset 2", "d2", datasetGroup1, true);
		DatasetEntity dataset3 = createDatasetEntity("dataset 3", "d3", datasetGroup2, true);
		
		Query query;
		List<FieldEntity> res;
		
		em.getTransaction().begin();
		query = em.createNamedQuery(BY_GROUP).setParameter("datasetGroup", datasetGroup1);
		res = query.getResultList();
		em.getTransaction().commit();

		assertEquals(2, res.size());
		assertTrue(res.contains(dataset1) && res.contains(dataset2));
		
		em.getTransaction().begin();
		query = em.createNamedQuery(BY_GROUP).setParameter("datasetGroup", datasetGroup2);
		res = query.getResultList();
		em.getTransaction().commit();

		assertEquals(1, res.size());
		assertTrue(res.contains(dataset3));

		em.getTransaction().begin();
		query = em.createNamedQuery(NQ_GET_ALL_DATASET);
		res = query.getResultList();
		em.getTransaction().commit();

		assertEquals(3, res.size());
		assertTrue(res.contains(dataset1) && res.contains(dataset2) && res.contains(dataset3));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void namedQery_DatasetGroupEntity(){
		OwnerEntity owner1 = createOwnerEntity("owner1", "o1", null);
		OwnerEntity owner2 = createOwnerEntity("owner2", "o2", null);
		
		GroupEntity datasetGroup1 = createDatasetGroupEntity("datasetgroup1", "dg1", null, owner1);
		GroupEntity datasetGroup2 = createDatasetGroupEntity("datasetgroup2", "dg2", null, owner1);
		GroupEntity datasetGroup3 = createDatasetGroupEntity("datasetgroup3", "dg3", null, owner2);
		
		Query query;
		List<FieldEntity> res;
		
		em.getTransaction().begin();
		query = em.createNamedQuery(ALL);
		res = query.getResultList();
		em.getTransaction().commit();

		assertEquals(3, res.size());

		em.getTransaction().begin();
		query = em.createNamedQuery(BY_OWNER).setParameter("owner", owner1);
		res = query.getResultList();
		em.getTransaction().commit();

		assertEquals(2, res.size());
		assertTrue(res.contains(datasetGroup1) && res.contains(datasetGroup2));
		
		em.getTransaction().begin();
		query = em.createNamedQuery(BY_OWNER).setParameter("owner", owner2);
		res = query.getResultList();
		em.getTransaction().commit();

		assertEquals(1, res.size());
		assertTrue(res.contains(datasetGroup3));
	}


	@SuppressWarnings("unchecked")
	@Test
	public void namedQuery_ReportEntity(){
		OwnerEntity owner3 = createOwnerEntity("owner3", "o3", null);
		OwnerEntity owner4 = createOwnerEntity("owner4", "o4", null);
		GroupEntity datasetGroup1 = createDatasetGroupEntity("dataset2", "d2", null, owner4);
		GroupEntity datasetGroup2 = createDatasetGroupEntity("dataset", "d2", null, owner4);
		DatasetEntity dataset1 = createDatasetEntity("dataset3", "d1", datasetGroup2, true);
		DatasetEntity dataset2 = createDatasetEntity("dataset4", "d4", datasetGroup2, true);
		ReportEntity report1 = createReportEntity("message1", "email1", owner3, null, null, Status.UNREAD);
		ReportEntity report2 = createReportEntity("message2", "email2", owner4, datasetGroup1, null, Status.UNREAD);
		ReportEntity report3 = createReportEntity("message3", "email3", owner4, datasetGroup2, dataset1, Status.UNREAD);
		ReportEntity report4 = createReportEntity("message4", "email4", owner4, datasetGroup2, dataset2, Status.UNREAD);

		
		Query query;
		List<FieldEntity> res;
		ReportEntity oneRes; 
		
		
		em.getTransaction().begin();
		query = em.createNamedQuery(ReportEntity.GET_REPORTS_BY_OWNER);
		query.setParameter("owner", owner3);
		res = query.getResultList();
		em.getTransaction().commit();
		
		assertEquals(1, res.size());
		assertTrue(res.contains(report1));
		
		em.getTransaction().begin();
		query = em.createNamedQuery(ReportEntity.GET_REPORTS_BY_DATASET_GROUP);
		query.setParameter("datasetGroup", datasetGroup1);
		res = query.getResultList();
		em.getTransaction().commit();
		
		assertEquals(1, res.size());
		assertTrue(res.contains(report2));
		
		em.getTransaction().begin();
		query = em.createNamedQuery(ReportEntity.GET_REPORTS_BY_DATASET_GROUP);
		query.setParameter("datasetGroup", datasetGroup2);
		res = query.getResultList();
		em.getTransaction().commit();
		
		assertEquals(2, res.size());
		assertTrue(res.contains(report3) && res.contains(report4));
		
		em.getTransaction().begin();
		query = em.createNamedQuery(ReportEntity.GET_REPORTS_BY_DATASET);
		query.setParameter("dataset", dataset2);
		oneRes = (ReportEntity) query.getSingleResult();
		em.getTransaction().commit();
		
		assertEquals(report4, oneRes);
	}	
}
