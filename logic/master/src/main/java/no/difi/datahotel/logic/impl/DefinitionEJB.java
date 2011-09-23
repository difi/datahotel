package no.difi.datahotel.logic.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import no.difi.datahotel.logic.model.DefinitionEntity;

/**
 * This EJB works against the FeltType entity.
 */
@Stateless
public class DefinitionEJB extends AbstractJPAHandler {

	/**
	 * Retrieves the {@code DefinitionEntity} corresponding to the given ID
	 * 
	 * @param id
	 *            - The ID of the {@code DefinitionEntity} to retrieve.
	 * @return The corresponding {@code DefinitionEntity}, or null if none
	 *         exists.
	 */
	public DefinitionEntity getDefinition(long id) {
		return em.find(DefinitionEntity.class, id);
	}

	/**
	 * Retrieves all the {@code DefinitionEntity}s in the database
	 * 
	 * @return a List of all the {@code DefinitionEntity} in the tatabase
	 */
	@SuppressWarnings("unchecked")
	public List<DefinitionEntity> getAll() {
		Query query = em.createNamedQuery(DefinitionEntity.ALL);

		return query.getResultList();
	}

	/**
	 * @param shortName
	 * @return
	 */
	public DefinitionEntity getDefinitionByShortName(String shortName) {
		try {
			Query query = em.createNamedQuery(DefinitionEntity.BY_SHORTNAME);
			query.setParameter("shortName", shortName);
			
			return (DefinitionEntity) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Checks if a definitions shortName already is used, if it is not the
	 * {@code DefinitionEntity} will be persisted
	 * 
	 * @param defEntity
	 *            The {@code DefinitionEntity} to persist
	 * @return The {@code DefinitionEntity} that was persisted
	 * @throws IllegalArgumentException
	 *             - If the definition shortName already exists
	 */
	public DefinitionEntity persist(DefinitionEntity defEntity) throws IllegalArgumentException {
		if (getDefinitionByShortName(defEntity.getShortName()) != null)
			throw new IllegalArgumentException("Definition already exists");

		em.persist(defEntity);
		em.flush();
		return defEntity;
	}
}
