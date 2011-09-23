package no.difi.datahotel.logic.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.UserEntity;

@Stateless
public class UserEJB extends AbstractJPAHandler {

	public UserEntity getByIdent(String ident) {
		try {
			Query query = em.createNamedQuery(UserEntity.BY_IDENT);
			query.setParameter("ident", ident);

			return (UserEntity) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<UserEntity> getByOwner(OwnerEntity owner) {
		Query query = em.createNamedQuery(UserEntity.BY_OWNER);
		query.setParameter("owner", owner);

		return query.getResultList();
	}
}
