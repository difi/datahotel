package no.difi.datahotel.logic.impl;

import static no.difi.datahotel.logic.model.OwnerEntity.ALL;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import no.difi.datahotel.logic.model.OwnerEntity;

@Stateless
public class OwnerEJB extends AbstractJPAHandler {

	/**
	 * Retrieves the {@code OwnerEntity} corresponding to the given shortName
	 * 
	 * @param shortName
	 *            - The shortName of the {@code OwnerEntity} to retrieve.
	 * @return The corresponding {@code OwnerEntity}, or null if none exists.
	 */
	public OwnerEntity getOwnerByShortName(String shortName) {
		try {
			Query query = em.createNamedQuery(OwnerEntity.BY_SHORTNAME);
			query.setParameter("shortName", shortName);

			return (OwnerEntity) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public OwnerEntity getByOrganizationNumber(Long number) {
		try {
			Query query = em.createNamedQuery(OwnerEntity.BY_ORGNR);
			query.setParameter("orgnr", number);

			return (OwnerEntity) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Retrieves all {@link OwnerEntity}s from the database
	 * 
	 * @return - a list if {@code Owner}s
	 */
	@SuppressWarnings("unchecked")
	public List<OwnerEntity> getAll() {
		Query query = em.createNamedQuery(ALL);

		return query.getResultList();
	}
}
