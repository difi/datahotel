package no.difi.datahotel.client.report;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import no.difi.datahotel.logic.impl.DatasetEJB;
import no.difi.datahotel.logic.impl.GroupEJB;
import no.difi.datahotel.logic.impl.OwnerEJB;
import no.difi.datahotel.logic.impl.ReportEJB;
import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.ReportEntity;

@RequestScoped
@ManagedBean(name = "report")
public class ReportBean {

	private static Logger logger = Logger.getLogger(ReportBean.class.getSimpleName());

	@EJB
	private DatasetEJB datasetEJB;
	
	@EJB
	private OwnerEJB ownerEJB;
	
	@EJB
	private GroupEJB groupEJB;
	
	@EJB
	private ReportEJB reportEJB;

	private OwnerEntity owner;
	private GroupEntity group;
	private DatasetEntity dataset;

	private ReportEntity report = new ReportEntity();

	
	public ReportEntity getReport() {
		return report;
	}

	public void setReport(ReportEntity report) {
		this.report = report;
		logger.warning("ReportEntity#setReport: " + report);
	}

	
	public String getOwnerShortName() {
		if (owner != null)
			return owner.getShortName();
		return "";
	}

	public void setOwnerShortName(String ownerShortName) {
		logger.warning("ow: " + ownerShortName);
		owner = ownerEJB.getOwnerByShortName(ownerShortName);
		logger.warning(String.valueOf(owner));
	}

	
	public String getGroupShortName() {
		if (group != null)
			return group.getShortName();
		return "";
	}

	public void setGroupShortName(String groupShortName) {
		logger.warning("gr: " + groupShortName);
		if (this.owner != null)
			group = groupEJB.getDatasetGroup(groupShortName, owner);
		logger.warning(String.valueOf(group));
	}

	
	public String getDatasetShortName() {
		if (dataset != null)
			return dataset.getShortName();
		return "";
	}

	public void setDatasetShortName(String datasetShortName) {
		logger.warning("ds: " + datasetShortName);
		if (this.group != null)
			dataset = datasetEJB.getByShortNameAndGroup(datasetShortName, group);
		logger.warning(String.valueOf(dataset));
	}

	
	public OwnerEntity getOwner() {
		return owner;
	}

	public GroupEntity getGroup() {
		return group;
	}

	public DatasetEntity getDataset() {
		return dataset;
	}

	
	public String submit() {
		report.setOwner(owner);
		report.setDatasetGroup(group);
		report.setDataset(dataset);

		reportEJB.save(report);

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Takk for din tilbakemelding."));
		
		logger.log(Level.INFO, "ReportEntity#submitReport: " + report);

		return null;
	}
}
