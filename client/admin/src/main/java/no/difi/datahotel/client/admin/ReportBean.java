package no.difi.datahotel.client.admin;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.impl.ReportEJB;
import no.difi.datahotel.logic.model.ReportEntity;

@RequestScoped
@ManagedBean(name = "report")
public class ReportBean {

	@EJB
	private ReportEJB reportEJB;

	@ManagedProperty(value = "#{browse}")
	private BrowseBean browseBean;

	private Long id;

	public List<ReportEntity> getReports() {
		if (browseBean.getDataset().getId() != null)
			return reportEJB.getByDataset(browseBean.getDataset());
		if (browseBean.getGroup().getId() != null)
			return reportEJB.getByGroup(browseBean.getGroup());
		if (browseBean.getOwner().getId() != null)
			return reportEJB.getByOwner(browseBean.getOwner());

		return null;
	}

	public ReportEntity getReport() {
		return reportEJB.getReport(id);
	}

	public void setBrowseBean(BrowseBean browseBean) {
		this.browseBean = browseBean;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}
}
