package no.difi.datahotel.client.wizard;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.impl.GroupEJB;
import no.difi.datahotel.logic.model.GroupEntity;

@ManagedBean(name = "group")
@RequestScoped
public class GroupBean {

	@EJB
	private GroupEJB groupEJB;

	@ManagedProperty(value = "#{user}")
	private UserBean userBean;

	private GroupEntity group = new GroupEntity();

	public List<GroupEntity> getAll() {
		return groupEJB.getByOwner(userBean.getOwner());
	}
	
	public String save() throws Exception {
		group.setOwner(userBean.getOwner());
		groupEJB.save(group);
		
		group.save();
		
		return "pretty:manage_group";
	}

	public GroupEntity getGroup() {
		return group;
	}

	public void setGroup(GroupEntity group) {
		this.group = group;
	}

	public void setGroupShortName(String shortName) {
		group = groupEJB.getDatasetGroup(shortName, userBean.getOwner());
	}

	public String getGroupShortName() {
		return group.getShortName();
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
}
