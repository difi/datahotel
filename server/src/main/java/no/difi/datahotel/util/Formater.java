package no.difi.datahotel.util;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.difi.datahotel.util.formater.CSVFormater;
import no.difi.datahotel.util.formater.HTMLFormater;
import no.difi.datahotel.util.formater.JSONFormater;
import no.difi.datahotel.util.formater.JSONPFormater;
import no.difi.datahotel.util.formater.TextFormater;
import no.difi.datahotel.util.formater.XMLFormater;
import no.difi.datahotel.util.formater.YAMLFormater;


/**
 * This class represents a valid data format.
 * It can format objects into the specified values, as well
 * as generate valid responses and error responses for Jersey.
 */
public enum Formater {
	
	XML("xml", "text/xml;charset=UTF-8", new XMLFormater()),
	CSV("csv", "text/plain;charset=UTF-8", new CSVFormater()),
	CSVCORRECT(null, "text/csv;charset=UTF-8", new CSVFormater()),
	JSON("json", "application/json;charset=UTF-8", new JSONFormater()),
	JSONP("jsonp", "application/json;charset=UTF-8", new JSONPFormater()),
	YAML("yaml", "text/plain;charset=UTF-8", new YAMLFormater()),
	TEXT_HTML("html", "text/html;charset=UTF-8", new HTMLFormater()),
	TEXT_PLAIN(null, "text/plain;charset=UTF-8", new TextFormater());

	private static Logger logger = Logger.getLogger(Formater.class.getSimpleName());

	private String type;
	private String mime;
	private FormaterInterface cls;

	private Formater(String type, String mime, FormaterInterface cls) {
		this.type = type;
		this.mime = mime;
		this.cls = cls;
	}

	/**
	 * Gets a new dataformat based on a mime type.
	 * @param type Mime type (ie. json)
	 * @return Returns a new DataFormat enum.
	 */
	public static Formater get(String type) {
		for (Formater t : Formater.values())
			if (type.equals(t.type))
				return t;

		return TEXT_PLAIN;
	}

	/**
	 * Gets the type of this DataFormat.
	 * @return Returns the type of this DataFormat.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Gets the correct Mime type for this DataFormat.
	 * @return Returns the correct Mime type for this DataFormat.
	 */
	public String getMime() {
		return this.mime;
	}

	/**
	 * Formats an object if support for it has been implemented.
	 * @param object Object to format.
	 * @param metadata Metadata.
	 * @return Returns a string representation of the object supplied.
	 */
	public String format(Object object, RequestContext context) {
		try
		{
			return cls.format(object, context);
		} catch (Exception e)
		{
			logger.log(Level.WARNING, "Exception in error presenter.", e);
			return formatError(e.getMessage(), context);
		}
	}
	
	/**
	 * Formats an object into an error.
	 * @param error
	 * @param metadata
	 * @return
	 */
	public String formatError(String error, RequestContext context) {
		try
		{
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("error", error);

			return cls.format(message, context);
		} catch (Exception e)
		{
			logger.log(Level.WARNING, "Exception in error presenter.");
			return "Error";
		}
	}
}