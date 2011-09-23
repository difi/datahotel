package no.difi.datahotel.client.admin;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.impl.DatasetEJB;
import no.difi.datahotel.logic.impl.GroupEJB;
import no.difi.datahotel.logic.impl.OwnerEJB;
import no.difi.datahotel.logic.impl.UserEJB;
import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.UserEntity;

@RequestScoped
@ManagedBean(name = "browse")
public class BrowseBean {

	@EJB
	private UserEJB userEJB;
	@EJB
	private OwnerEJB ownerEJB;
	@EJB
	private GroupEJB groupEJB;
	@EJB
	private DatasetEJB datasetEJB;

	private OwnerEntity owner = new OwnerEntity();
	private GroupEntity group = new GroupEntity();
	private DatasetEntity dataset = new DatasetEntity();

	// METHODS FOR OWNER

	public List<OwnerEntity> getOwners() {
		return ownerEJB.getAll();
	}

	public void setOwnerShortName(String shortName) {
		this.owner = ownerEJB.getOwnerByShortName(shortName);
	}

	public String getOwnerShortName() {
		return this.owner.getShortName();
	}

	public OwnerEntity getOwner() {
		return this.owner;
	}

	public void setOwner(OwnerEntity owner) {
		this.owner = owner;
	}

	public void saveOwner() throws Exception {
		owner.save();
		ownerEJB.save(owner);
	}

	public List<UserEntity> getUsers() {
		return userEJB.getByOwner(owner);
	}

	// METHODS FOR GROUP

	public List<GroupEntity> getGroups() {
		return groupEJB.getByOwner(owner);
	}

	public void setGroupShortName(String shortName) {
		this.group = groupEJB.getDatasetGroup(shortName, owner);
	}

	public String getGroupShortName() {
		return this.group.getShortName();
	}

	public GroupEntity getGroup() {
		return this.group;
	}

	public void setGroup(GroupEntity group) {
		this.group = group;
	}

	public void saveGroup() throws Exception {
		group.setOwner(owner);
		group.save();
		groupEJB.save(group);
	}

	// METHODS FOR DATASET

	public List<DatasetEntity> getDatasets() {
		return datasetEJB.getDatasetsByDatasetGroup(group);
	}

	public void setDatasetShortName(String shortName) {
		this.dataset = datasetEJB.getByShortNameAndGroup(shortName, group);
	}

	public String getDatasetShortName() {
		return this.dataset.getShortName();
	}

	public DatasetEntity getDataset() {
		return this.dataset;
	}

	public void setDataset(DatasetEntity dataset) {
		this.dataset = dataset;
	}
}
