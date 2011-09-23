package no.difi.datahotel.client.admin;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.impl.SettingEJB;
import no.difi.datahotel.logic.model.SettingEntity;

@RequestScoped
@ManagedBean(name = "setting")
public class SettingBean {

	@EJB
	private SettingEJB settingEJB;

	private String key;
	private String value;

	public List<SettingEntity> getAll() {
		return settingEJB.getList();
	}

	public void save() {
		settingEJB.set(key, value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
