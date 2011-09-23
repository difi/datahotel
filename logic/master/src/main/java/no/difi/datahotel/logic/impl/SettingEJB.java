package no.difi.datahotel.logic.impl;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import no.difi.datahotel.logic.model.SettingEntity;

@Stateless
public class SettingEJB extends AbstractJPAHandler {

	@SuppressWarnings("unchecked")
	public List<SettingEntity> getList() {
		return em.createNamedQuery(SettingEntity.ALL).getResultList();
	}

	public HashMap<String, String> getAll() {
		HashMap<String, String> values = new HashMap<String, String>();

		for (SettingEntity s : getList())
			values.put(s.getKey(), s.getValue());

		return values;
	}

	public String get(String key) {
		try {
			Query query = em.createNamedQuery(SettingEntity.BY_KEY);
			query.setParameter("key", key);

			return ((SettingEntity) query.getSingleResult()).getValue();
		} catch (NoResultException e) {
			return null;
		}
	}

	public String get(String key, String fallback) {
		String res = get(key);
		return res == null ? fallback : res;
	}

	@SuppressWarnings("unchecked")
	public void set(String key, String value) {
		Query query = em.createNamedQuery(SettingEntity.BY_KEY);
		query.setParameter("key", key);
		for (SettingEntity s : (List<SettingEntity>) query.getResultList())
			em.remove(s);

		if (value == null || !value.isEmpty())
			em.persist(new SettingEntity(key, value));
	}
}
