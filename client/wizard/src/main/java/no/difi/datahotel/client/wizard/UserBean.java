package no.difi.datahotel.client.wizard;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.pretty.PrettyContext;

import no.difi.datahotel.logic.impl.LoginEJB;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.UserEntity;

@ManagedBean(name = "user")
@SessionScoped
public class UserBean {

	private static Logger logger = Logger.getLogger(UserBean.class.getSimpleName());

	@EJB
	private LoginEJB loginEJB;

	private UserEntity user;
	private OwnerEntity owner;

	public String login(boolean required) {
		if (owner != null)
			return null;

		try {
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			HttpServletRequest request = (HttpServletRequest) context.getRequest();
			PrettyContext pretty = PrettyContext.getCurrentInstance();

			if (request.getParameter("token") != null) {
				user = loginEJB.getUser(request.getParameter("token"));
				owner = user.getOwner();
				
				return "pretty:" + pretty.getCurrentMapping().getId();
			} else {
				if (required)
					context.redirect("/login?goto=/wizard");
			}
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		
		return null;
	}
	
	public String logout() {
		owner = null;
		user = null;
		
		return "pretty:home";
	}

	public UserEntity getUser() {
		return this.user;
	}

	public OwnerEntity getOwner() {
		return this.owner;
	}

	public boolean isHasAccess() {
		return this.user != null;
	}
	
	public String getFrontpage() {
		return owner == null ? "/faces/index.jsf" : "/faces/manage/owner/list.jsf";
	}
}
