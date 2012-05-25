package no.difi.datahotel.util.jersey;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents a valid data format.
 * It can format objects into the specified values, as well
 * as generate valid responses and error responses for Jersey.
 */
public enum DataFormat {
	
	XML("xml", "text/xml;charset=UTF-8", new XMLObject()),
	CSV("csv", "text/plain;charset=UTF-8", new CSVObject()),
	CSVCORRECT(null, "text/csv;charset=UTF-8", new CSVObject()),
	JSON("json", "application/json;charset=UTF-8", new JSONObject()),
	JSONP("jsonp", "application/json;charset=UTF-8", new JSONPObject()),
	YAML("yaml", "text/plain;charset=UTF-8", new YAMLObject()),
	TEXT_HTML("html", "text/html;charset=UTF-8", new HTMLObject()),
	TEXT_PLAIN(null, "text/plain;charset=UTF-8", new TextObject());

	private static Logger logger = Logger.getLogger(DataFormat.class.getSimpleName());

	private String type;
	private String mime;
	private FormaterInterface cls;

	private DataFormat(String type, String mime, FormaterInterface cls) {
		this.type = type;
		this.mime = mime;
		this.cls = cls;
	}

	/**
	 * Gets a new dataformat based on a mime type.
	 * @param type Mime type (ie. json)
	 * @return Returns a new DataFormat enum.
	 */
	public static DataFormat get(String type) {
		for (DataFormat t : DataFormat.values())
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