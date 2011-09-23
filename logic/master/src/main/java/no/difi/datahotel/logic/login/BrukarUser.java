package no.difi.datahotel.logic.login;

import java.io.Serializable;

public class BrukarUser implements Serializable {

	private static final long serialVersionUID = -1041501815574709014L;

	private String ident;
	
	private String name;

	private boolean admin = false;

	private Long organizationNumber;
	private String organizationName;
	private String organizationShort;
	private String organizationUrl;

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Long getOrganizationNumber() {
		return organizationNumber;
	}

	public void setOrganizationNumber(Long organizationNumber) {
		this.organizationNumber = organizationNumber;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationShort() {
		return organizationShort;
	}

	public void setOrganizationShort(String organizationShort) {
		this.organizationShort = organizationShort;
	}

	public String getOrganizationUrl() {
		return organizationUrl;
	}

	public void setOrganizationUrl(String organizationUrl) {
		this.organizationUrl = organizationUrl;
	}
}
