package no.difi.datahotel.client.slave;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.slave.MetadataEJB;
import no.difi.datahotel.logic.slave.MetadataEJB.DatasetHolder;
import no.difi.datahotel.util.bridge.Dataset;
import no.difi.datahotel.util.bridge.Group;
import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Fields;
import no.difi.datahotel.util.bridge.Owner;

@ManagedBean(name = "info")
@RequestScoped
public class InfoBean {

	@EJB
	private MetadataEJB metadataEJB;

	private String ownerShortName;
	private String groupShortName;
	private String datasetShortName;
	private String defShortName;

	private Owner owner;
	private Group group;
	private Dataset dataset;
	private Fields fields;
	private Definition def;

	public List<Owner> getOwners() {
		return metadataEJB.getOwners();
	}

	public List<Group> getGroups() {
		return metadataEJB.getGroups(getOwnerShortName());
	}

	public List<Dataset> getDatasets() {
		return metadataEJB
				.getDatasets(getOwnerShortName(), getGroupShortName());
	}

	public List<DatasetHolder> getLast() {
		return metadataEJB.getLast(5);
	}

	public List<Definition> getDefinitions() {
		return metadataEJB.getDefinitions();
	}

	public List<Dataset> getDefDatasets() {
		return metadataEJB.getDefinitionUsage(getDefShortName());
	}

	// GETTERS AND SETTERS! :)

	public String getOwnerShortName() {
		return ownerShortName;
	}

	public void setOwnerShortName(String owner) {
		this.owner = metadataEJB.getOwner(owner);
		this.ownerShortName = owner;
	}

	public String getGroupShortName() {
		return groupShortName;
	}

	public void setGroupShortName(String group) {
		this.group = metadataEJB.getGroup(getOwnerShortName(), group);
		this.groupShortName = group;
	}

	public String getDatasetShortName() {
		return datasetShortName;
	}

	public void setDatasetShortName(String dataset) {
		this.dataset = metadataEJB.getDataset(getOwnerShortName(),
				getGroupShortName(), dataset);
		this.fields = metadataEJB.getFields(getOwnerShortName(),
				getGroupShortName(), dataset);
		this.datasetShortName = dataset;
	}

	public void setDefShortName(String defName) {
		this.def = metadataEJB.getDefinition(defName);
		this.defShortName = defName;
	}

	public String getDefShortName() {
		return defShortName;
	}

	public Owner getOwner() {
		return owner;
	}

	public Group getGroup() {
		return group;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public Definition getDef() {
		return def;
	}

	public List<Field> getFields() {
		return fields.getFields();
	}
}
