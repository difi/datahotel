package no.difi.datahotel.logic.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import no.difi.datahotel.util.bridge.Metadata;

@NamedQueries({
		@NamedQuery(name = DatasetEntity.NQ_GET_ALL_DATASET, query = "SELECT d FROM Dataset d"),
		@NamedQuery(name = DatasetEntity.NQ_GET_ALL_UNRESOLVED_DATASETS, query = "SELECT d FROM Dataset d"),
		@NamedQuery(name = DatasetEntity.BY_GROUP, query = "SELECT d FROM Dataset d WHERE d.datasetGroup = :datasetGroup ORDER BY d.name"),
		@NamedQuery(name = DatasetEntity.BY_SHORTNAME_AND_GROUP, query = "SELECT d FROM Dataset d WHERE d.shortName = :shortName AND d.datasetGroup = :datasetGroup"),
		@NamedQuery(name = DatasetEntity.LASTUPDATED, query = "SELECT d FROM Dataset d ORDER BY d.lastUpdated DESC") })
@Entity(name = "Dataset")
public class DatasetEntity implements JPAEntity {

	/** NamedQuery that returns all {@linkplain DatasetEntity}s that has */
	public static final String NQ_GET_ALL_UNRESOLVED_DATASETS = "getAllUnresolvedDatasets";

	/**
	 * NamedQuery that returns all the {@linkplain DatasetEntity}s corresponding
	 * to the {@link GroupEntity}
	 * <p>
	 * identified by the provided {@code DatasetGroup}. Parameters:
	 * <ol>
	 * <li>datasetGroup, the datasetGroup</li>
	 * </ol>
	 */
	@Transient
	public static final String BY_GROUP = "DATASET_BY_GROUP";

	/** NamedQuery that returns all {@linkplain DatasetEntity}s */
	@Transient
	public static final String NQ_GET_ALL_DATASET = "DATASET_ALL";

	@Transient
	public static final String BY_SHORTNAME_AND_GROUP = "DATASET_BY_SHORTNAME_AND_GROUP";

	public static final String LASTUPDATED = "LASTUPDATED";

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

	@Column(name = "lastUpdated", nullable = false)
	@Basic
	private long lastUpdated;

	@OneToOne
	@JoinColumn(name = "currentVersion")
	private VersionEntity currentVersion;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "datasetGroup", nullable = false)
	private GroupEntity datasetGroup;

	@Column(name = "description")
	@Basic
	private String description;

	@Column(name = "isVisible", nullable = false)
	@Basic
	private Boolean deleted = Boolean.TRUE;

	@OneToMany(mappedBy = "dataset")
	@JoinColumn
	private List<VersionEntity> versions = new LinkedList<VersionEntity>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "dataset")
	private Set<ReportEntity> reports = new HashSet<ReportEntity>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String datasetName) {
		this.name = datasetName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName.toLowerCase();
	}

	public long getLastEdited() {
		return lastUpdated;
	}

	public void setLastEdited(long lastEdited) {
		this.lastUpdated = lastEdited;
	}

	public VersionEntity getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(VersionEntity currentVersion) {
		this.currentVersion = currentVersion;
	}

	public GroupEntity getDatasetGroup() {
		return datasetGroup;
	}

	public void setDatasetGroup(GroupEntity datasetGroup) {
		this.datasetGroup = datasetGroup;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setVisible(Boolean visible) {
		this.deleted = visible;
	}

	public Boolean getVisible() {
		return deleted;
	}

	public void setVersions(List<VersionEntity> versions) {
		this.versions = versions;
	}

	public List<VersionEntity> getVersions() {
		return versions;
	}

	public void setReports(Set<ReportEntity> reports) {
		this.reports = reports;
	}

	public Set<ReportEntity> getReports() {
		return reports;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof DatasetEntity))
			return false;
		DatasetEntity d = (DatasetEntity) o;
		return this.shortName.equals(d.getShortName()) && this.datasetGroup.equals(d.getDatasetGroup());
	}

	@Override
	public int hashCode() {
		return shortName.hashCode() + 3 * datasetGroup.hashCode();
	}

	public void save() throws Exception {
		Metadata metadata = new Metadata();
		metadata.setName(this.getName());
		metadata.setDescription(this.getDescription());
		metadata.setLocation(Metadata.getLocation(getDatasetGroup().getOwner().getShortName(), getDatasetGroup()
				.getShortName(), getShortName()));
		metadata.save();
	}
}
