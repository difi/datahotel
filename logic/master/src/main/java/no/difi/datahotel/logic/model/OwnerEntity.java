package no.difi.datahotel.logic.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import no.difi.datahotel.util.bridge.Metadata;

@NamedQueries({ @NamedQuery(name = OwnerEntity.ALL, query = "SELECT o FROM Owner o"),
		@NamedQuery(name = OwnerEntity.BY_SHORTNAME, query = "SELECT o FROM Owner o WHERE o.shortName = :shortName"),
		@NamedQuery(name = OwnerEntity.BY_ORGNR, query = "SELECT o FROM Owner o WHERE o.organizationNumber = :orgnr") })
@Entity(name = "Owner")
public class OwnerEntity implements JPAEntity {

	public static final String ALL = "OWNER_ALL";
	public static final String BY_SHORTNAME = "OWNER_BY_SHORTNAME";
	public static final String BY_ORGNR = "OWNER_BY_ORGNR";

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

	@Column(name = "url")
	@Basic
	private String url;

	@Column(name = "orgnr")
	@Basic
	private Long organizationNumber;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private Set<GroupEntity> datasetGroups = new LinkedHashSet<GroupEntity>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
	private Set<ReportEntity> reports = new HashSet<ReportEntity>();

	public Set<GroupEntity> getDatasetGroups() {
		return datasetGroups;
	}

	public void setDatasetGroups(Set<GroupEntity> datasetGroups) {
		this.datasetGroups = datasetGroups;
	}

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

	public void setReports(Set<ReportEntity> reports) {
		this.reports = reports;
	}

	public Set<ReportEntity> getReports() {
		return reports;
	}

	public Long getOrganizationNumber() {
		return organizationNumber;
	}

	public void setOrganizationNumber(Long organizationNumber) {
		this.organizationNumber = organizationNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof OwnerEntity))
			return false;
		OwnerEntity d = (OwnerEntity) o;
		return this.shortName.equals(d.getShortName());
	}

	public void save() throws Exception {
		Metadata metadata = new Metadata();
		metadata.setName(this.getName());
		metadata.setLocation(Metadata.getLocation(this.getShortName()));
		metadata.setUrl(this.getUrl());
		metadata.save();
	}
}
