package no.difi.datahotel.logic.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import no.difi.datahotel.logic.model.FieldEntity;
import no.difi.datahotel.logic.model.VersionEntity;

@Stateless
public class FieldEJB extends AbstractJPAHandler {

	@SuppressWarnings("unchecked")
	public List<FieldEntity> getByVersion(VersionEntity version)
	{
		Query query = em.createNamedQuery(FieldEntity.BY_VERSION);
		query.setParameter("version", version);
		
		return query.getResultList();
	}
	
}
