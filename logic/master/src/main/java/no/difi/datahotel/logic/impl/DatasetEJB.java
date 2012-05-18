package no.difi.datahotel.logic.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.VersionEntity;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

/**
 * The EJB with the logic for accessing the db with metadata about the datasets.
 */
@Stateless
public class DatasetEJB extends AbstractJPAHandler {

	/**
	 * Retrieves all the {@code DatasetEntity}s corresponding to the given
	 * {@code DatasetGroup}
	 * 
	 * @param GroupEntity
	 *            - The {@code DatasetGroupEntity} of the {@code datasetGroup}
	 *            to retrieve.
	 * @return The corresponding {@code datasetGroup}, or null if none exists.
	 */
	@SuppressWarnings("unchecked")
	public List<DatasetEntity> getDatasetsByDatasetGroup(GroupEntity datasetGroup) {
		Query query = em.createNamedQuery(DatasetEntity.BY_GROUP);
		query.setParameter("datasetGroup", datasetGroup);
		
		return query.getResultList();
	}

	/**
	 * Retrieves the {@code DatasetGroupEntity} with the given shortName in the
	 * {@code DatasetGroupEntity}
	 * 
	 * @param shortName
	 *            - The shortName of the dataset
	 * @param group
	 *            - the group containing the dataset
	 * @return The corresponding {@code DatasetEntity}, or null if none exists.
	 */
	public DatasetEntity getByShortNameAndGroup(String shortName, GroupEntity group) {
		try {
			Query query = em.createNamedQuery(DatasetEntity.BY_SHORTNAME_AND_GROUP);
			query.setParameter("shortName", shortName);
			query.setParameter("datasetGroup", group);
			
			return (DatasetEntity) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Retrieves all the {@code DatasetEntity}s corresponding to the given
	 * {@code DatasetGroup}
	 * 
	 * @param id
	 *            - The ID of the {@code DatasetEntity} to retrieve.
	 * @return The corresponding {@code DatasetEntity}, or null if none exists.
	 */
	@SuppressWarnings("unchecked")
	public List<DatasetEntity> getAll() {
		Query query = em.createNamedQuery(DatasetEntity.NQ_GET_ALL_DATASET);

		return query.getResultList();
	}

	/**
	 * Retreives the 5 last updated {@code DatasetEntity}s
	 * 
	 * @return A list of the 5 last {@code DatasetEntity}, or null if there are
	 *         none.
	 */
	@SuppressWarnings("unchecked")
	public List<DatasetEntity> getFiveLastDatasets() {
		Query query = em.createNamedQuery(VersionEntity.NQ_GET_FIVE_LAST_DATASETS_BY_VERSIONS);
		query.setMaxResults(5);

		return query.getResultList();
	}
	
	public long getLastUpdated() {
		try {
			Query query = em.createNamedQuery(DatasetEntity.LASTUPDATED);
			query.setHint(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);
			query.setHint(QueryHints.REFRESH, HintValues.TRUE);
			query.setMaxResults(1);
			return ((DatasetEntity) query.getSingleResult()).getLastEdited();
		} catch (NoResultException e) {
			return 0;
		}
	}
}
