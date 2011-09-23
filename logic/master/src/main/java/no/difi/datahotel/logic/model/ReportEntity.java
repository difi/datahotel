package no.difi.datahotel.logic.model;

import static javax.persistence.CascadeType.*;

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

@NamedQueries({
	@NamedQuery(name=ReportEntity.GET_REPORTS_BY_OWNER, query="SELECT r FROM Report r WHERE r.owner = :owner"),
	@NamedQuery(name=ReportEntity.GET_REPORTS_BY_DATASET_GROUP, query="SELECT r FROM Report r WHERE r.datasetGroup = :datasetGroup"),
	@NamedQuery(name=ReportEntity.GET_REPORTS_BY_DATASET, query="SELECT r FROM Report r WHERE r.dataset = :dataset")
})
@Entity(name="Report")
public class ReportEntity implements JPAEntity {

	/**NamedQuery that returns all the {@linkplain ReportEntity}s corresponding to the {@link OwnerEntity}.
	 * <p>
	 * Parameters:
	 * <ol>
	 * <li> owner, the {@code ReportEntity}</li>
	 * </ol>*/
	public static final String GET_REPORTS_BY_OWNER = "getReportsByOwner";

	/**NamedQuery that returns all the {@linkplain ReportEntity}s corresponding to the {@link GroupEntity}
	 * <p>
	 * Parameters:
	 * <ol>
	 * <li> datasetGroup, the {@code DatasetGroupEntity}</li>
	 * </ol>*/
	public static final String GET_REPORTS_BY_DATASET_GROUP = "getReportsByDatasetGroup";

	/**NamedQuery that returns all the {@linkplain ReportEntity}s corresponding to the {@link DatasetEntity}
	 * <p>
	 * Parameters:
	 * <ol>
	 * <li> dataset, the {@code DatasetEntity}</li>
	 * </ol>*/
	public static final String GET_REPORTS_BY_DATASET = "getReportsByDataset";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="message", nullable=false)
	@Basic
	private String message;

	@Column(name="email", nullable=false)
	@Basic
	private String email;

	@Column(name="status", nullable=false)
	@Basic
	private Status status = Status.UNREAD;

	@JoinColumn(name="owner", nullable=false)
	@ManyToOne(cascade={MERGE, REFRESH})
	private OwnerEntity owner;

	@JoinColumn(name="datasetGroup")
	@ManyToOne(cascade={MERGE, REFRESH})
	private GroupEntity datasetGroup;

	@JoinColumn(name="dataset")
	@ManyToOne(cascade={MERGE, REFRESH})
	private DatasetEntity dataset;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public OwnerEntity getOwner() {
		return owner;
	}

	public void setOwner(OwnerEntity owner) {
		this.owner = owner;
	}

	public GroupEntity getDatasetGroup() {
		return datasetGroup;
	}

	public void setDatasetGroup(GroupEntity datasetGroup) {
		this.datasetGroup = datasetGroup;
	}

	public DatasetEntity getDataset() {
		return dataset;
	}

	public void setDataset(DatasetEntity dataset) {
		this.dataset = dataset;
	}

	public static enum Status{
		UNREAD, READ, DELETED, SPAM;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof ReportEntity))return false;
		ReportEntity r = (ReportEntity) o;
		return 
		this.message.equals(r.getMessage()) && this.email.equals(r.getEmail()) 
				&& this.status.equals(r.getStatus()) && this.owner.equals(r.getOwner()) 
				&& (this.datasetGroup == null) ? r.getDatasetGroup() == null : this.datasetGroup.equals(r.getDatasetGroup())
				&& (this.dataset == null) ? r.getDataset() == null : this.dataset.equals(r.getDataset());
	}
	
	@Override
	public int hashCode() {
		return message.hashCode() 
		+ 3 * email.hashCode() 
		+ 5 * status.hashCode() 
		+ 7 * owner.hashCode() 
		+ ((datasetGroup == null) ? 0 : (11 * datasetGroup.hashCode()))
		+ ((dataset == null) ? 0 : (13 * dataset.hashCode()));
	}
}
