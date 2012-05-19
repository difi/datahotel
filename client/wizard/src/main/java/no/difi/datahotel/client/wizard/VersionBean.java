package no.difi.datahotel.client.wizard;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.impl.DatasetEJB;
import no.difi.datahotel.logic.impl.VersionEJB;
import no.difi.datahotel.logic.impl.XmlEJB;
import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.VersionEntity;

import org.apache.myfaces.custom.fileupload.UploadedFile;

@ManagedBean(name = "version")
@RequestScoped
public class VersionBean {

	@EJB
	private DatasetEJB datasetEJB;

	@EJB
	private VersionEJB versionEJB;
	
	@EJB
	private XmlEJB xmlEJB;

	@ManagedProperty(value = "#{dataset}")
	private DatasetBean datasetBean;

	private VersionEntity version;
	private UploadedFile file;

	public String upload() throws Exception {
		if (file != null) {
			version = versionEJB.create(file.getInputStream(),
					datasetBean.getDataset());
	
			return "pretty:manage_version";
		}
		return null;
	}
	
	public List<String[]> getTop() throws Exception {
		if (version != null)
			return versionEJB.getTop(version);
		return null;
	}

	public List<VersionEntity> getAll() {
		return versionEJB.getByDataset(datasetBean.getDataset());
	}
	
	public String publish() throws Exception {
		DatasetEntity dataset = datasetBean.getDataset(); 
		dataset.setCurrentVersion(version);
		dataset.setLastEdited(new Date().getTime());
		datasetEJB.save(dataset);
	
		versionEJB.saveFile(version);
		
		xmlEJB.saveFieldsToDisk(dataset, version);
		
		return "pretty:manage_dataset";
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public VersionEntity getVersion() {
		return version;
	}

	public void setVersion(VersionEntity version) {
		this.version = version;
	}

	public void setVersionIdent(Long ident) {
		this.version = versionEJB.getByDatasetAndTimestamp(
				datasetBean.getDataset(), ident);
	}

	public Long getVersionIdent() {
		return version.getVersion();
	}

	public void setDatasetBean(DatasetBean datasetBean) {
		this.datasetBean = datasetBean;
	}
}
