package no.difi.datahotel.logic.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({ @NamedQuery(name = FieldEntity.BY_VERSION, query = "SELECT f FROM Field f WHERE f.version = :version ORDER BY f.id") })
@Entity(name = "Field")
public class FieldEntity implements JPAEntity {

	public static final String BY_VERSION = "FIELD_BY_VERSION";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", nullable = false)
	@Basic
	private String name;

	@Column(name = "shortName", nullable = false)
	@Basic
	private String shortName;

	@ManyToOne(cascade = { MERGE, REFRESH })
	@JoinColumn(name = "version", nullable = false)
	private VersionEntity version;

	@Column(name = "description")
	@Basic
	private String description;

	@Column(name = "isSearchable", nullable = false)
	@Basic
	private Boolean searchable = Boolean.FALSE;

	@Column(name = "isGroupable", nullable = false)
	@Basic
	private Boolean groupable = Boolean.FALSE;

	@Column(name = "isPrimaryIndexKey", nullable = false)
	@Basic
	private Boolean primaryIndexKey = Boolean.FALSE;

	@Column(name = "edited", nullable = false)
	@Basic
	private Boolean edited = Boolean.FALSE;
	
	@ManyToOne(cascade = { REFRESH, MERGE })
	@JoinColumn(name = "definitions")
	private DefinitionEntity definition;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public VersionEntity getVersion() {
		return version;
	}

	public void setVersion(VersionEntity version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setSearchable(Boolean scarchable) {
		this.searchable = scarchable;
	}

	public Boolean getGroupable() {
		return groupable;
	}

	public void setGroupable(Boolean groupable) {
		this.groupable = groupable;
	}

	public Boolean getPrimaryIndexKey() {
		return primaryIndexKey;
	}

	public void setPrimaryIndexKey(Boolean primaryIndexKey) {
		this.primaryIndexKey = primaryIndexKey;
	}

	public Boolean getEdited() {
		return edited;
	}

	public void setEdited(Boolean edited) {
		this.edited = edited;
	}

	public void setDefinition(DefinitionEntity definition) {
		this.definition = definition;
	}

	public DefinitionEntity getDefinition() {
		return definition;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof FieldEntity))
			return false;
		if (o == this)
			return true;

		FieldEntity f = (FieldEntity) o;

		if (id == null && f.id == null)
			return true;
		if (id == null ^ f.id == null)
			return false;
		
		return this.id.equals(f.id);
	}

	@Override
	public int hashCode() {
		return shortName.hashCode() + 3 * version.hashCode();
	}
}
