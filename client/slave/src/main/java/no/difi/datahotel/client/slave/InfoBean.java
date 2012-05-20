package no.difi.datahotel.client.slave;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.slave.FieldEJB;
import no.difi.datahotel.logic.slave.MetadataEJB;
import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Metadata;
import no.difi.datahotel.util.bridge.MetadataLight;

@ManagedBean(name = "info")
@RequestScoped
public class InfoBean {

	@EJB
	private MetadataEJB metadataEJB;
	@EJB
	private FieldEJB fieldEJB;

	private String ownerShortName;
	private String groupShortName;
	private String datasetShortName;
	private String defShortName;

	private Metadata owner, group, dataset;
	private List<Field> fields;
	private Definition def;

	public List<MetadataLight> getOwners() {
		return metadataEJB.getChildren();
	}

	public List<MetadataLight> getGroups() {
		return metadataEJB.getChildren(getOwnerShortName());
	}

	public List<MetadataLight> getDatasets() {
		return metadataEJB.getChildren(getOwnerShortName(), getGroupShortName());
	}

	public List<Definition> getDefinitions() {
		return fieldEJB.getDefinitions();
	}

	public List<String> getDefDatasets() {
		return fieldEJB.getUsage(getDefShortName());
	}

	// GETTERS AND SETTERS! :)

	public String getOwnerShortName() {
		return ownerShortName;
	}

	public void setOwnerShortName(String owner) {
		this.owner = metadataEJB.getChild(owner);
		this.ownerShortName = owner;
	}

	public String getGroupShortName() {
		return groupShortName;
	}

	public void setGroupShortName(String group) {
		this.group = metadataEJB.getChild(getOwnerShortName(), group);
		this.groupShortName = group;
	}

	public String getDatasetShortName() {
		return datasetShortName;
	}

	public void setDatasetShortName(String dataset) {
		this.dataset = metadataEJB.getChild(getOwnerShortName(), getGroupShortName(), dataset);
		this.fields = fieldEJB.getFields(getOwnerShortName(), getGroupShortName(), dataset);
		this.datasetShortName = dataset;
	}

	public void setDefShortName(String defName) {
		this.def = fieldEJB.getDefinition(defName);
		this.defShortName = defName;
	}

	public String getDefShortName() {
		return defShortName;
	}

	public Metadata getOwner() {
		return owner;
	}

	public Metadata getGroup() {
		return group;
	}

	public Metadata getDataset() {
		return dataset;
	}

	public Definition getDef() {
		return def;
	}

	public List<Field> getFields() {
		return fields;
	}
}
