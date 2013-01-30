package no.difi.datahotel.util;

import static no.difi.datahotel.util.Formater.JSON;;


@SuppressWarnings("serial")
public class DatahotelException extends RuntimeException {

	private int status = 500;
	private Formater formater = JSON;

	public DatahotelException(String message) {
		super(message);
	}
	
	public DatahotelException(int status, String message) {
		super(message);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public Formater getFormater() {
		return formater;
	}

	public DatahotelException setFormater(Formater formater) {
		this.formater = formater;
		return this;
	}
}
