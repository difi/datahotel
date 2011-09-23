package no.difi.datahotel.logic.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import no.difi.brukar.scribe.BrukarApi;
import no.difi.datahotel.logic.login.BrregOrganization;
import no.difi.datahotel.logic.login.BrukarUser;
import no.difi.datahotel.logic.model.AccessEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.UserEntity;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.svenson.JSONParser;

@Stateless
public class LoginEJB {

	private static Logger logger = Logger.getLogger(LoginEJB.class.getSimpleName());

	@EJB
	private SettingEJB settingEJB;
	@EJB
	private UserEJB userEJB;
	@EJB
	private OwnerEJB ownerEJB;

	@PersistenceContext(unitName = "datahotel")
	protected EntityManager em;

	public void save(Token accessToken) {
		em.persist(new AccessEntity(accessToken));
	}

	public UserEntity getUser(String token) throws Exception
	{
		BrukarUser bUser = getSimpleUser(token);
		if (bUser == null)
			return null;
		
		UserEntity user = userEJB.getByIdent(bUser.getIdent());
		if (user == null)
		{
			user = new UserEntity();
			user.setIdent(bUser.getIdent());
			user.setName(bUser.getName());
			user.setOwner(ownerEJB.getByOrganizationNumber(bUser.getOrganizationNumber()));
			
			if (user.getOwner() == null)
			{
				OwnerEntity owner = new OwnerEntity();
				owner.setName(bUser.getOrganizationName());
				owner.setShortName(bUser.getOrganizationShort());
				owner.setOrganizationNumber(bUser.getOrganizationNumber());
				owner.setUrl(bUser.getOrganizationUrl());
				ownerEJB.save(owner);
				owner.save();
				
				user.setOwner(owner);
			}
			
			userEJB.save(user);
		}
		
		return user;
	}
	
	public BrukarUser getSimpleUser(String token) {
		try {
			if ("empty".equals(token) && settingEJB.get("midifi_host") == null)
			{
				BrukarUser user = new BrukarUser();
				user.setAdmin(true);
				user.setIdent("default");
				user.setName("Default");
				user.setOrganizationName("Default");
				user.setOrganizationNumber(42L);
				user.setOrganizationShort("default");
				user.setOrganizationUrl("http://www.domain.com/");
				return user;
			}
			
			AccessEntity access = (AccessEntity) em.createNamedQuery(AccessEntity.BY_TOKEN).setParameter("token", token)
					.getSingleResult();
			em.remove(access);

			Token accessToken = access.getAccessToken();
			return _getUser(accessToken);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	BrukarUser _getUser(Token accessToken) {
		BrukarApi brukarApi = new BrukarApi(settingEJB.get("midifi_host"));
		OAuthService service = new ServiceBuilder().provider(brukarApi).apiKey(settingEJB.get("midifi_key"))
				.apiSecret(settingEJB.get("midifi_secret")).build();

		OAuthRequest request = new OAuthRequest(Verb.GET, brukarApi.getHost() + "server/oauth/user");
		service.signRequest(accessToken, request);
		Response response = request.send();

		BrukarUser user = new BrukarUser();

		Map m = JSONParser.defaultJSONParser().parse(Map.class, response.getBody());
		user.setIdent((String) m.get("id"));
		user.setName((String) m.get("name"));
		user.setAdmin(String.valueOf(m.get("role_value")).equals("10"));

		List groups = (List) m.get("groups");
		if (groups != null)
			for (Object group : groups) {
				if (((String) group).startsWith("org:")) {
					try {
						String orgstring = (String) group;
						int orgnr = Integer.parseInt(orgstring.substring(4, orgstring.indexOf("/")));
						BrregOrganization org = new BrregOrganization(orgnr);

						user.setOrganizationName(org.get("Navn/foretaksnavn"));
						user.setOrganizationNumber(Long.parseLong(org.get("Organisasjonsnummer").replace(" ", "")));
						if (org.get("Internettadresse") != null)
							user.setOrganizationUrl("http://" + org.get("Internettadresse"));
						user.setOrganizationShort(org.getShort());
					} catch (IOException e) {
						logger.log(Level.WARNING, e.getMessage(), e);
					}

					break;
				}
			}

		return user;
	}
}
