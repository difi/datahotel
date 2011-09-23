package no.difi.datahotel.logic.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.scribe.model.Token;

@Entity(name = "Access")
@NamedQueries({ @NamedQuery(name = AccessEntity.BY_TOKEN, query = "SELECT a FROM Access a WHERE a.token = :token") })
public class AccessEntity {

	public final static String BY_TOKEN = "ACCESS_BY_TOKEN";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String token;
	private String secret;

	public AccessEntity() {

	}

	public AccessEntity(Token token) {
		this.token = token.getToken();
		this.secret = token.getSecret();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Transient
	public Token getAccessToken() {
		return new Token(token, secret);
	}
}
