package no.difi.datahotel.util;

import no.difi.datahotel.util.formater.*;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents a valid data format.
 * It can format objects into the specified values, as well
 * as generate valid responses and error responses for Jersey.
 */
public enum Formater {
	
	XML("xml", "text/xml", new XMLFormater()),
	CSV("csv", "text/plain", new CSVFormater()),
	CSVCORRECT(null, "text/csv", new CSVFormater()),
	JSON("json", "application/json", new JSONFormater()),
	JSONP("jsonp", "application/json", new JSONPFormater()),
	YAML("yaml", "text/plain", new YAMLFormater());

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

		throw new DatahotelException(404, "Format not found."); 
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
		return this.mime + ";charset=UTF-8";
	}

	/**
	 * Formats an object if support for it has been implemented.
	 * @param object Object to format.
     * @param context Context.
     * @return Returns a string representation of the object supplied.
	 */
	public String format(Object object, RequestContext context) {
		try
		{
			return cls.format(object, context);
		} catch (Exception e)
		{
            logger.log(Level.WARNING, e.getMessage() + " - Format: " + type + " - " + e.getClass().getSimpleName() + " - " + e.getStackTrace()[0].toString());
            return formatError(e, context);
		}
	}
	
	/**
	 * Formats an object into an error.
     * @param exception
     * @param context
     * @return
	 */
	public String formatError(Exception exception, RequestContext context) {
		try
		{
			return cls.format(new SimpleError(exception.getMessage()), context);
		} catch (Exception e)
		{
            logger.log(Level.WARNING, e.getMessage() + " - Format: " + type + " - " + e.getClass().getSimpleName() + " - " + e.getStackTrace()[0].toString());
            return "Error";
        }
	}
}