package no.difi.datahotel.util.jersey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.MetadataLight;

public class HTMLObject implements FormaterInterface {

	private static Tab[] tabs = new Tab[] { new Tab("Data", "/"), new Tab("Definition", "/_defs") };
	private static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String format(Object object, RequestContext context) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><html></head>");

		if (!"nostyle".equals(context.getCallback()))
			sb.append("<link rel=\"stylesheet\" href=\"/style.css\" type=\"text/css\" />");

		sb.append("</head><body>");

		if (object instanceof Map) {
			Map<String, String> values = (Map<String, String>) object;
			for (String key : values.keySet())
				sb.append("<div class=\"").append(key).append("\">").append(values.get(key)).append("</div>");
		} else {
			if (object instanceof CSVData) {
				sb.append(formatCSVData((CSVData) object, context));
			} else if (object instanceof List<?>) {
				List list = (List) object;
				if (list.size() > 0) {
					if (list.get(0) instanceof MetadataLight)
						sb.append(formatMetadata((List<MetadataLight>) list, context));
					if (list.get(0) instanceof Field)
						sb.append(formatField((List<Field>) list, context));
					if (list.get(0) instanceof Definition)
						sb.append(formatDefinition((List<Definition>) list, context));
				}
			}
		}

		sb.append("</body></html>");

		return sb.toString();
	}

	private String formatCSVData(CSVData data, RequestContext context) {
		StringBuilder sb = new StringBuilder();

		sb.append(getTabs("Data"));

		sb.append("<div class=\"top\"><form action=\"/api/html/").append(context.getMetadata().getLocation())
				.append("\" method=\"get\"><input type=\"text\" name=\"query\" value=\"")
				.append(context.getQuery() == null ? "" : context.getQuery())
				.append("\" /><input type=\"submit\" value=\"Search\" /></form>")
				.append(context.getMetadata().getLocation()).append("</div>");

		if (data.getEntries().size() == 0)
			return sb.toString();

		sb.append("<table>");

		sb.append("<tr class=\"head\">");
		for (Field field : context.getFields())
			sb.append("<th class=\"").append(field.getShortName()).append("\"><span title=\"")
					.append(field.getContent()).append("\">").append(field.getName()).append("</span></th>");
		sb.append("</tr>");

		int i = 0;
		for (Map<String, String> row : data.getEntries()) {
			i++;
			sb.append("<tr class=\"").append(i % 2 == 1 ? "odd" : "even").append("\">");
			for (Field field : context.getFields())
				sb.append("<td class=\"").append(field.getShortName()).append("\">")
						.append(row.get(field.getShortName())).append("</td>");
			sb.append("</tr>");
		}

		sb.append("</table>");

		return sb.toString();
	}

	private String formatMetadata(List<MetadataLight> list, RequestContext context) {
		StringBuilder sb = new StringBuilder();

		sb.append(getTabs("Data"));

		sb.append("<div class=\"top\">");
		sb.append(context.getMetadata() != null ? context.getMetadata().getLocation() : "Home");
		sb.append("</div>");

		int i = 0;
		for (MetadataLight m : list) {
			i++;
			sb.append("<div class=\"metadata ").append(i % 2 == 1 ? "odd" : "even").append("\">");

			sb.append("<h3><a href=\"/api/html/").append(m.getLocation()).append("\">").append(m.getName())
					.append("</a>").append("</h3>");

			sb.append("<div class=\"meta\">");
			sb.append("<span class=\"updated\" title=\"Last updated\">")
					.append(date.format(new Date(m.getUpdated() * 1000))).append("</span>");
			if (m.getUrl() != null)
				sb.append("<span class=\"url\"><a href=\"").append(m.getUrl()).append("\" rel=\"nofollow\">")
						.append(niceUrl(m.getUrl())).append("</a></span>");
			sb.append("</div>");

			if (m.getDescription() != null)
				sb.append("<div class=\"description\">").append(m.getDescription()).append("</div>");

			sb.append("</div>");
		}

		return sb.toString();
	}

	private String formatField(List<Field> list, RequestContext context) {
		StringBuilder sb = new StringBuilder();

		sb.append(getTabs("Data"));

		sb.append("<div class=\"top\">");
		sb.append(context.getMetadata() != null ? context.getMetadata().getLocation() : "Home");
		sb.append("</div>");

		sb.append("<table>");
		sb.append("<tr class=\"head\"><th>Name</th><th>Short</th><th>Definition</th><th>Description</th><th>Groupable</th><th>Searchable</th></tr>");
		int i = 0;
		for (Field f : list) {
			i++;
			sb.append("<tr class=\"").append(i % 2 == 1 ? "odd" : "even").append("\">");
			sb.append("<td>").append(f.getName()).append("</td>");
			sb.append("<td>").append(f.getShortName()).append("</td>");
			sb.append("<td>").append(f.getDefinition().getName()).append("</td>");
			sb.append("<td>").append(f.getContent()).append("</td>");
			sb.append("<td>").append(f.getGroupable() ? "True" : "False").append("</td>");
			sb.append("<td>").append(f.getSearchable() ? "True" : "False").append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");

		return sb.toString();
	}

	private String formatDefinition(List<Definition> list, RequestContext context) {
		StringBuilder sb = new StringBuilder();

		sb.append(getTabs("Definition"));

		sb.append("<div class=\"top\">Definitions</div>");

		sb.append("<table>");
		sb.append("<tr class=\"head\"><th>Name</th><th>Short</th><th>Description</th></tr>");
		int i = 0;
		for (Definition f : list) {
			i++;
			sb.append("<tr class=\"").append(i % 2 == 1 ? "odd" : "even").append("\">");
			sb.append("<td>").append(f.getName()).append("</td>");
			sb.append("<td>").append(f.getShortName()).append("</td>");
			sb.append("<td>").append(f.getDescription()).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");

		return sb.toString();
	}

	private String getTabs(String tab) {
		StringBuilder sb = new StringBuilder();

		sb.append("<ul class=\"tabs\">");
		sb.append("<li class=\"meta\"><a href=\"https://github.com/difi/datahotel\">Code</a></li>");

		for (Tab t : tabs)
			sb.append("<li class=\"").append(t.name.equals(tab) ? "tab active" : "tab").append("\"><a href=\"/api/html")
					.append(t.location).append("\">").append(t.name).append("</a></li>");

		sb.append("</ul>");

		return sb.toString();
	}

	private String niceUrl(String url) {
		if (url.endsWith("/"))
			url = url.substring(0, url.length() - 1);
		if (url.startsWith("http://"))
			url = url.substring(7);
		if (url.startsWith("https://"))
			url = url.substring(8);
		if (url.startsWith("www."))
			url = url.substring(4);

		return url;
	}

	private static class Tab {
		public String name, location;

		public Tab(String name, String location) {
			this.name = name;
			this.location = location;
		}
	}
}
