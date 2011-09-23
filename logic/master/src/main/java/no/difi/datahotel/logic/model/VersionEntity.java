package no.difi.datahotel.logic.model;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@NamedQueries({
		@NamedQuery(name = VersionEntity.BY_DATASET, query = "SELECT v FROM Version v WHERE v.dataset = :dataset"),
		@NamedQuery(name = VersionEntity.NQ_GET_FIVE_LAST_DATASETS_BY_VERSIONS, query = "SELECT v.dataset FROM Version v ORDER BY v.version DESC"),
		@NamedQuery(name = VersionEntity.NQ_GET_VERSION_BY_VERSION, query = "SELECT v FROM Version v WHERE v.version = :version"),
		@NamedQuery(name = VersionEntity.BY_DATASET_AND_TIMESTAMP, query = "SELECT v FROM Version v WHERE v.dataset = :dataset AND v.version = :timestamp ORDER BY v.version") })
@Entity(name = "Version")
public class VersionEntity implements JPAEntity {

	public static final String BY_DATASET = "VERSION_BY_DATASET";
	public static final String NQ_GET_FIVE_LAST_DATASETS_BY_VERSIONS = "getFiveLastDatasetsByVersion";
	public static final String NQ_GET_VERSION_BY_VERSION = "getVersionByVersion";
	public static final String BY_DATASET_AND_TIMESTAMP = "VERSION_BY_DATASET_AND_TIMESTAMP";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "version", nullable = false, unique = true)
	@Basic
	private Long version;

	@OneToOne
	@JoinColumn(name = "dataset")
	private DatasetEntity dataset;

	@OneToMany(mappedBy = "version", cascade = { ALL }, orphanRemoval = true)
	private List<FieldEntity> fields = new ArrayList<FieldEntity>();

	@Column
	@Basic
	private boolean inProgress;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public Date getVersionDate() {
		return new Date(version);
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public DatasetEntity getDataset() {
		return dataset;
	}

	public void setDataset(DatasetEntity dataset) {
		this.dataset = dataset;
	}

	public List<FieldEntity> getFields() {
		return fields;
	}

	public void setFields(List<FieldEntity> fields) {
		this.fields = fields;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof VersionEntity))
			return false;
		if (o == this)
			return true;

		VersionEntity v = (VersionEntity) o;

		if (version == null && v.version == null)
			return true;
		if (version == null || v.version == null)
			return false;

		return version.equals(v.version);
	}

	@Override
	public int hashCode() {
		if (version == null || dataset == null)
			return -1;

		return version.hashCode() + 3 * dataset.hashCode();
	}
}
