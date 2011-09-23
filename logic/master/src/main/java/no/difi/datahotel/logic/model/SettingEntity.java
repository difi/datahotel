package no.difi.datahotel.logic.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

@NamedQueries({ @NamedQuery(name = SettingEntity.ALL, query = "SELECT s FROM Setting s ORDER BY s.key"),
		@NamedQuery(name = SettingEntity.BY_KEY, query = "SELECT s FROM Setting s WHERE s.key = :key") })
@Entity(name = "Setting")
public class SettingEntity implements Serializable, JPAEntity {

	@Transient private static final long serialVersionUID = 7333996646254904017L;

	@Transient public static final String ALL = "SETTING_ALL";
	@Transient public static final String BY_KEY = "SETTING_BY_KEY";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="sKey")
	@Basic
	private String key;
	
	@Column(name="sValue")
	@Basic
	private String value;

	public SettingEntity() {

	}

	public SettingEntity(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
