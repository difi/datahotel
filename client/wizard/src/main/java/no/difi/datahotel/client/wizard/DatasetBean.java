package no.difi.datahotel.client.wizard;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.impl.DatasetEJB;
import no.difi.datahotel.logic.model.DatasetEntity;

@ManagedBean(name = "dataset")
@RequestScoped
public class DatasetBean {

	@EJB
	private DatasetEJB datasetEJB;

	@ManagedProperty(value = "#{group}")
	private GroupBean groupBean;

	private DatasetEntity dataset = new DatasetEntity();

	public List<DatasetEntity> getAll() {
		return datasetEJB.getDatasetsByDatasetGroup(groupBean.getGroup());
	}
	
	public String save() throws Exception
	{
		dataset.setDatasetGroup(groupBean.getGroup());
		datasetEJB.save(dataset);

		dataset.save();
		
		return "pretty:manage_dataset";
	}

	public DatasetEntity getDataset() {
		return dataset;
	}

	public void setDataset(DatasetEntity dataset) {
		this.dataset = dataset;
	}

	public void setDatasetShortName(String shortName) {
		dataset = datasetEJB.getByShortNameAndGroup(shortName,
				groupBean.getGroup());
	}

	public String getDatasetShortName() {
		return dataset.getShortName();
	}

	public void setGroupBean(GroupBean groupBean) {
		this.groupBean = groupBean;
	}
}
