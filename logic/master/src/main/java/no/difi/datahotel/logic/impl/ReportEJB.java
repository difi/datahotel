package no.difi.datahotel.logic.impl;

import static no.difi.datahotel.logic.model.ReportEntity.*;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.ReportEntity;

@Stateless
public class ReportEJB extends AbstractJPAHandler {

	public ReportEntity getReport(long id) {
		return em.find(ReportEntity.class, id);
	}

	/**
	 * Retrieves the {@code ReportEntity} corresponding to the given
	 * {@code OwnerEntity}
	 * 
	 * @param owner
	 *            - The {@code OwnerEntity} of the {@code DatasetGroupEntity} to
	 *            retrieve.
	 * @return The corresponding {@code ReportEntity}, or null if none exists.
	 */
	@SuppressWarnings("unchecked")
	public List<ReportEntity> getByOwner(OwnerEntity owner) {
		Query query = em.createNamedQuery(GET_REPORTS_BY_OWNER);
		query.setParameter("owner", owner);

		return query.getResultList();
	}

	/**
	 * Retrieves the {@code ReportEntity} corresponding to the given
	 * {@code DatasetEntity}
	 * 
	 * @param dataset
	 *            - The {@code DatasetEntity} of the {@code ReportEntity} to
	 *            retrieve.
	 * @return The corresponding {@code ReportEntity}, or null if none exists.
	 */
	@SuppressWarnings("unchecked")
	public List<ReportEntity> getByDataset(DatasetEntity dataset) {
		Query query = em.createNamedQuery(GET_REPORTS_BY_DATASET);
		query.setParameter("dataset", dataset);
		
		return query.getResultList();
	}

	/**
	 * Retrieves the {@code ReportEntity} corresponding to the given
	 * {@code DatasetGroupEntity}
	 * 
	 * @param dataset
	 *            - The {@code DatasetGroupEntity} of the {@code ReportEntity}
	 *            to retrieve.
	 * @return The corresponding {@code ReportEntity}, or null if none exists.
	 */
	@SuppressWarnings("unchecked")
	public List<ReportEntity> getByGroup(GroupEntity datasetGroup) {
		Query query = em.createNamedQuery(GET_REPORTS_BY_DATASET_GROUP);
		query.setParameter("datasetGroup", datasetGroup);
		
		return query.getResultList();
	}
}
