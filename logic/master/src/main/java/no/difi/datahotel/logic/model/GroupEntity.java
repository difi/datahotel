package no.difi.datahotel.logic.model;

import java.util.ArrayList;
import java.util.HashSet;
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
import javax.persistence.Transient;

import no.difi.datahotel.util.bridge.Group;
import no.difi.datahotel.util.bridge.Metadata;

@NamedQueries({
		@NamedQuery(name = GroupEntity.ALL, query = "SELECT dg FROM DatasetGroup dg ORDER BY dg.name"),
		@NamedQuery(name = GroupEntity.BY_OWNER, query = "SELECT dg FROM DatasetGroup dg WHERE dg.owner = :owner"),
		@NamedQuery(name = GroupEntity.BY_SHORTNAME_AND_OWNER, query = "SELECT dg FROM DatasetGroup dg WHERE dg.owner = :owner AND dg.shortName = :shortName") })
@Entity(name = "DatasetGroup")
public class GroupEntity implements JPAEntity {
	/** NamedQuery that returns all {@linkplain GroupEntity}s */
	@Transient
	public static final String ALL = "GROUP_ALL";

	/**
	 * NamedQuery that returns all the {@linkplain GroupEntity} corresponding to
	 * the {@linkplain OwnerEntity}
	 * <p>
	 * Parameters:
	 * <ol>
	 * <li>owner, the owner of the DatasetGroup</li>
	 * </ol>
	 */
	@Transient
	public static final String BY_OWNER = "GROUP_BY_OWNER";

	@Transient
	public static final String BY_SHORTNAME_AND_OWNER = "GROUP_BY_SHORTNAME_AND_OWNER";

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

	@Column(name = "url")
	@Basic
	private String url;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "owner", nullable = false)
	private OwnerEntity owner;

	@OneToMany(mappedBy = "datasetGroup", cascade = { CascadeType.ALL })
	private List<DatasetEntity> datasets = new ArrayList<DatasetEntity>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "datasetGroup")
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

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public OwnerEntity getOwner() {
		return owner;
	}

	public void setOwner(OwnerEntity owner) {
		this.owner = owner;
	}

	public List<DatasetEntity> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<DatasetEntity> datasets) {
		this.datasets = datasets;
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
		if (!(o instanceof GroupEntity))
			return false;
		GroupEntity d = (GroupEntity) o;
		return this.shortName.equals(d.getShortName()) && this.owner.equals(d.getOwner());
	}

	@Override
	public int hashCode() {
		return this.shortName.hashCode() + 3 * owner.hashCode();
	}

	public void save() throws Exception {
		Metadata metadata = new Metadata();
		metadata.setName(this.getName());
		metadata.setUrl(this.getUrl());
		metadata.setLocation(this.getOwner().getShortName() + "/" + this.getShortName());
		metadata.save();
		
		Group group = new Group();
		group.setName(this.getName());
		group.setShortName(this.getShortName());
		// group.setDescripion(this.)
		group.setUrl(this.getUrl());
		group.setOwner(this.getOwner().getShortName());
		group.save();
	}
}
