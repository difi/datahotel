package no.difi.datahotel.client.admin;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import no.difi.datahotel.logic.impl.LoginEJB;
import no.difi.datahotel.logic.login.BrukarUser;

@SessionScoped
@ManagedBean(name = "user")
public class UserBean implements Serializable {

	private static final long serialVersionUID = -767910784373359956L;

	private static Logger logger = Logger.getLogger(UserBean.class.getSimpleName());

	@EJB
	private LoginEJB loginEJB;

	private boolean isAdmin;

	/**
	 * Metode som verifiserer at kun de med nødvendig tilgang har tilgang.
	 */
	public String login() {
		try {
			// Hvis brukeren allerede er admin trenger vi ikke gjøre mer.
			if (isAdmin)
				return null;

			// Hent kontekst som er nødvendig.
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			HttpServletRequest request = (HttpServletRequest) context.getRequest();

			// Forsøke å logge inn bruker om det finnes en token
			if (request.getParameter("token") != null) {
				// Hente bruker og kopier rettigheter
				BrukarUser user = loginEJB.getSimpleUser(request.getParameter("token"));
				if (user != null)
					this.isAdmin = user.isAdmin();

				// Dersom brukeren ikke er administrator etter kopiering er det
				// bare til å fjerne brukeren.
				if (!isAdmin)
					context.redirect("/");
			} else {
				// La brukeren få mulighet til å autentisere seg.
				context.redirect("/login?goto=/admin");
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}

		return "pretty:home";
	}

	public void logout() {
		this.isAdmin = false;
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("/");
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}
}
