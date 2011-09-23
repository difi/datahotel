package no.difi.datahotel.logic.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;

import org.apache.commons.io.filefilter.AndFileFilter;

@Stateless
public class GroupEJB extends AbstractJPAHandler {

	/**
	 * Retrieves the {@code DatasetGroupEntity} with the given shortName
	 * {@link AndFileFilter} {@code OwnerEntity}
	 * 
	 * @param shortname
	 *            - The shortName of the dataset
	 * @param owner
	 *            - the owner of the dataset
	 * @return The corresponding {@code DatasetGroupEntity}, or null if none
	 *         exists.
	 */
	public GroupEntity getDatasetGroup(String shortName, OwnerEntity owner) {
		try {
			Query query = em.createNamedQuery(GroupEntity.BY_SHORTNAME_AND_OWNER);
			query.setParameter("shortName", shortName);
			query.setParameter("owner", owner);

			return (GroupEntity) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Retrieves the {@code DatasetGroupEntity} corresponding to the given
	 * {@code OwnerEntity}
	 * 
	 * @param owner
	 *            - The {@code OwnerEntity} of the {@code DatasetGroupEntity} to
	 *            retrieve.
	 * @return The corresponding {@code DatasetGroupEntity}, or null if none
	 *         exists.
	 */
	@SuppressWarnings("unchecked")
	public List<GroupEntity> getByOwner(OwnerEntity owner) {
		Query query = em.createNamedQuery(GroupEntity.BY_OWNER);
		query.setParameter("owner", owner);

		return query.getResultList();
	}
}
