package no.difi.datahotel.client.login;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.difi.brukar.scribe.BrukarApi;
import no.difi.datahotel.logic.impl.LoginEJB;
import no.difi.datahotel.logic.impl.SettingEJB;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

@WebServlet(urlPatterns = "/")
public class Servlet extends HttpServlet {

	private static final long serialVersionUID = 420369486342380743L;

	private static Logger logger = Logger.getLogger(Servlet.class.getSimpleName());

	private OAuthService service;

	@EJB
	private LoginEJB loginEJB;
	@EJB
	private SettingEJB settingEJB;

	protected HttpServletRequest req;
	protected HttpServletResponse res;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		this.req = req;
		this.res = res;

		if (settingEJB.get("midifi_host") == null)
		{
			String url = (String) req.getParameter("goto");
			url += (url.contains("?") ? "&" : "?") + "token=empty";
			res.sendRedirect(url);
			return;
		}
		
		BrukarApi brukarApi = new BrukarApi(settingEJB.get("midifi_host"));
		service = new ServiceBuilder().provider(brukarApi)
				.apiKey(settingEJB.get("midifi_key"))
				.apiSecret(settingEJB.get("midifi_secret"))
				.callback(req.getRequestURL().toString()).build();

		if (req.getParameter("goto") != null)
			requestLogin();
		else if (req.getParameter("oauth_token") != null)
			loadUser();
		else
			relocate();
	}

	protected void requestLogin() throws IOException {
		Token requestToken = service.getRequestToken();
		req.getSession(true).setAttribute("token", requestToken);
		req.getSession(true).setAttribute("goto", req.getParameter("goto"));

		res.sendRedirect(service.getAuthorizationUrl(requestToken));
	}

	protected void loadUser() throws IOException {
		try {
			Token requestToken = (Token) req.getSession(true).getAttribute("token");
			if (requestToken != null) {
				Token accessToken = service.getAccessToken(requestToken, new Verifier("dummy"));

				loginEJB.save(accessToken);

				String url = (String) req.getSession(true).getAttribute("goto");
				url += (url.contains("?") ? "&" : "?") + "token=" + accessToken.getToken();
				logger.info(url);
				res.sendRedirect(url);

				return;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}

		relocate();
	}

	protected void relocate() throws IOException {
		res.sendRedirect(req.getRequestURI().replace("/login", ""));
	}
}
