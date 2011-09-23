package no.difi.datahotel.logic.model;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@NamedQueries({
		@NamedQuery(name = DefinitionEntity.BY_NAME, query = "SELECT d FROM Definition d WHERE d.name = :name"),
		@NamedQuery(name = DefinitionEntity.BY_SHORTNAME, query = "SELECT d FROM Definition d WHERE d.shortName = :shortName"),
		@NamedQuery(name = DefinitionEntity.ALL, query = "SELECT d FROM Definition d") })
@Entity(name = "Definition")
public class DefinitionEntity implements JPAEntity {

	/** NamedQuery that returns all {@linkplain DefinitionEntity}s */
	@Transient
	public static final String ALL = "DEF_ALL";

	/**
	 * NamedQuery that returns all the {@linkplain DefinitionEntity}s
	 * corresponding to the given name
	 * <p>
	 * identified by the provided {@code DatasetGroup}. Parameters:
	 * <ol>
	 * <li>name, the name of the {@code DefinitionEntity}</li>
	 * </ol>
	 */
	@Transient
	public static final String BY_NAME = "DEF_BY_NAME";

	@Transient
	public static final String BY_SHORTNAME = "DEF_BY_SHORTNAME";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", nullable = false, unique = true)
	@Basic
	private String name;

	@Column(name = "shortName", nullable = false, unique = true)
	@Basic
	private String shortName;

	@Column(name = "description", nullable = false)
	@Basic
	private String description;

	@OneToMany(mappedBy = "definition")
	private List<FieldEntity> fields;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<FieldEntity> getFields() {
		return fields;
	}

	public void setFields(List<FieldEntity> fields) {
		this.fields = fields;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof DefinitionEntity))
			return false;
		if (o == this)
			return true;

		DefinitionEntity d = (DefinitionEntity) o;
		
		return String.valueOf(this.shortName).equals(String.valueOf(d.shortName));
	}

	@Override
	public int hashCode() {
		return shortName.hashCode();
	}
}
