package no.difi.datahotel.logic.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import no.difi.datahotel.logic.model.JPAEntity;

public abstract class AbstractJPAHandler {
	@PersistenceContext(unitName = "datahotel")
	protected EntityManager em;

	public JPAEntity save(JPAEntity o)
	{
		if (o.getId() == null)
			em.persist(o);
		else
			em.merge(o);

		em.flush();
		return o;
	}

	/**
	 * Persists the given {@linkplain JPAEntity} in the database
	 * 
	 * @param jpaEntity
	 *            - the {@code JPAEntity} to persist
	 * @return - the persisted {@code JPAEntity}
	 */
	@Deprecated
	public JPAEntity persist(JPAEntity jpaEntity) {
		em.persist(jpaEntity);
		em.flush();
		return jpaEntity;
	}

	/**
	 * Persists the given list of {@linkplain JPAEntity}s in the database
	 * 
	 * @param jpaEntities
	 *            - the {@code List}{@code JPAEntity} to persist
	 * @return - the persisted {@code JPAEntity} list
	 */
	public List<JPAEntity> persist(List<JPAEntity> jpaEntities) {
		for (int i = 0; i < jpaEntities.size(); i++) {
			em.persist(jpaEntities.get(i));
			if (i == 1000)
				em.flush();
		}
		em.flush();

		return jpaEntities;
	}

	/**
	 * Merge the given {@linkplain JPAEntity} with the one in the database
	 * 
	 * @param jpaEntity
	 *            - the {@code JPAEntity} to merge
	 * @return - the merged {@code JPAEntity}
	 */
	@Deprecated
	public JPAEntity merge(JPAEntity jpaEntity) {
		em.merge(jpaEntity);
		em.flush();
		return jpaEntity;
	}

	/**
	 * Merge the given list of {@linkplain JPAEntity}s with the one in the
	 * database
	 * 
	 * @param jpaEntities
	 *            - the {@code List}{@code JPAEntity} to merge
	 * @return - the merged {@code JPAEntity} list
	 */
	public List<JPAEntity> merge(List<JPAEntity> jpaEntities) {
		for (int i = 0; i < jpaEntities.size(); i++) {
			em.merge(jpaEntities.get(i));
			if (i == 1000)
				em.flush();
		}
		em.flush();

		return jpaEntities;
	}

	/**
	 * Refresh the given {@linkplain JPAEntity} in the database
	 * 
	 * @param jpaEntity
	 *            - the {@code JPAEntity} to refresh
	 * @return - the refreshed {@code JPAEntity}
	 */
	public JPAEntity refresh(JPAEntity jpaEntity) {
		em.refresh(jpaEntity);
		em.flush();
		return jpaEntity;
	}

	/**
	 * Refresh the given list of {@linkplain JPAEntity}s in the database
	 * 
	 * @param jpaEntities
	 *            - the {@code List}{@code JPAEntity} to refresh
	 * @return - the refreshed {@code JPAEntity} list
	 */
	public List<JPAEntity> refresh(List<JPAEntity> jpaEntities) {
		for (int i = 0; i < jpaEntities.size(); i++) {
			em.refresh(jpaEntities.get(i));
			if (i == 1000)
				em.flush();
		}
		em.flush();

		return jpaEntities;
	}
}
