package no.difi.datahotel.util.formater;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import no.difi.datahotel.model.Result;
import no.difi.datahotel.model.DefinitionLight;
import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.Metadata;
import no.difi.datahotel.model.MetadataLight;
import no.difi.datahotel.util.FormaterInterface;
import no.difi.datahotel.util.RequestContext;

public class HTMLFormater implements FormaterInterface {

	private static Tab[] tabs = new Tab[] { // new Tab("Home", "/"),
	new Tab("Data", "/api/html") // , new Tab("Definition", "/api/html/_def")
			, new Tab("API", "/api") };
	private static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String format(Object object, RequestContext context) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><html><head>");

		sb.append("<link rel=\"stylesheet\" href=\"/style.css\" type=\"text/css\" />");

		sb.append("</head><body>");

		if (object instanceof Map) {
			Map<String, String> values = (Map<String, String>) object;
			for (String key : values.keySet())
				sb.append("<div class=\"").append(key).append("\">").append(values.get(key)).append("</div>");
		} else {
			if (object instanceof Result) {
				sb.append(formatResult((Result) object, context));
			} else if (object instanceof List<?>) {
				List list = (List) object;
				if (list.size() > 0) {
					if (list.get(0) instanceof MetadataLight)
						sb.append(formatMetadata((List<MetadataLight>) list, context));
					if (list.get(0) instanceof FieldLight)
						sb.append(formatField((List<FieldLight>) list, context));
					if (list.get(0) instanceof DefinitionLight)
						sb.append(formatDefinition((List<DefinitionLight>) list, context));
				}
			}
		}

		sb.append("</body></html>");

		return sb.toString();
	}

	private String formatResult(Result data, RequestContext context) {
		StringBuilder sb = new StringBuilder();

		sb.append(getTabs("Data"));

		sb.append("<div class=\"top\"><div class=\"holder\">");

		sb.append("<form action=\"/api/html/").append(context.getMetadata().getLocation())
				.append("\" method=\"get\"><input type=\"text\" name=\"query\" value=\"")
				.append(context.getQuery() == null ? "" : context.getQuery())
				.append("\" /><input type=\"submit\" value=\"Search\" /></form>");

		sb.append("<h1>Datahotel</h1><h2>");
		sb.append("<a href=\"/api/html\">Data</a>");

		String l = "";

		Metadata m = context.getMetadata();
		while (!"".equals(m.getLocation())) {
			l = " / <a href=\"/api/html/" + m.getLocation() + "\">" + (l.equals("") ? m.getName() : m.getShortName())
					+ "</a>" + l;
			m = m.getParent();
		}

		sb.append(l);

		sb.append("</h2></div></div>");

		if (data.getEntries().size() == 0)
			return sb.toString();

		sb.append("<table>");

		sb.append("<tr class=\"head\">");
		for (FieldLight field : context.getFields())
			sb.append("<th class=\"").append(field.getShortName()).append("\"><span title=\"")
					.append(field.getDescription()).append("\">").append(field.getName()).append("</span></th>");
		sb.append("</tr>");

		int i = 0;
		for (Map<String, String> row : data.getEntries()) {
			i++;
			sb.append("<tr class=\"").append(i % 2 == 1 ? "odd" : "even").append("\">");
			for (FieldLight field : context.getFields())
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

		sb.append("<div class=\"top\"><div class=\"holder\"><h1>Datahotel</h1><h2>");
		sb.append("<a href=\"/api/html\">Data</a>");

		if (context.getMetadata() != null) {
			String l = "";
			Metadata md = context.getMetadata();
			while (!"".equals(md.getLocation())) {
				l = " / <a href=\"/api/html/" + md.getLocation() + "\">"
						+ (l.equals("") ? md.getName() : md.getShortName()) + "</a>" + l;
				md = md.getParent();
			}
			sb.append(l);
		}

		sb.append("</h2></div></div>");

		sb.append("<div class=\"holder list\">");

		int i = 0;
		for (MetadataLight m : list) {
			if (m == null)
				System.out.println("Null in list");

			i++;
			sb.append("<div class=\"metadata ").append(i % 2 == 1 ? "odd" : "even").append("\">");

			sb.append("<h3><a href=\"/api/html/").append(m.getLocation()).append("\">").append(m.getName())
					.append("</a>").append("</h3>");

			sb.append("<div class=\"meta\">");
			sb.append("<span class=\"updated\" title=\"Last updated\">")
					.append(m.getUpdated() == null ? "Never" : date.format(new Date(m.getUpdated() * 1000)))
					.append("</span>");
			if (m.getUrl() != null)
				sb.append("<span class=\"url\"><a href=\"").append(m.getUrl()).append("\" rel=\"nofollow\">")
						.append(niceUrl(m.getUrl())).append("</a></span>");
			sb.append("</div>");

			if (m.getDescription() != null)
				sb.append("<div class=\"description\">").append(m.getDescription()).append("</div>");

			sb.append("</div>");
		}

		sb.append("</div>");

		return sb.toString();
	}

	private String formatField(List<FieldLight> list, RequestContext context) {
		StringBuilder sb = new StringBuilder();

		sb.append(getTabs("Data"));

		sb.append("<div class=\"top\"><div class=\"holder\"><h1>Datahotel</h1><h2>");
		sb.append("<a href=\"/api/html\">Data</a>");

		String l = "";

		Metadata m = context.getMetadata();
		while (!"".equals(m.getLocation())) {
			l = " / <a href=\"/api/html/" + m.getLocation() + "\">" + m.getShortName() + "</a>" + l;
			m = m.getParent();
		}

		sb.append(l).append(" / Fields</h2></div></div>");

		sb.append("<div class=\"holder list\">");

		int i = 0;
		for (FieldLight f : list) {
			i++;
			sb.append("<div class=\"").append(i % 2 == 1 ? "odd" : "even").append("\">");
			sb.append("<h3>").append(f.getName()).append(" (").append(f.getShortName()).append(")</h3>");
			sb.append("<div class=\"meta\">");
			if (f.getDefinition() != null)
				sb.append("<span><a href=\"/api/html/_defs/").append(f.getDefinition()).append("\">")
						.append(f.getDefinition()).append("</a></span>");
			if (f.getGroupable())
				sb.append("<span>Groupable</span>");
			if (f.getSearchable())
				sb.append("<span>Searchable</span>");
			sb.append("</div>");
			if (f.getDescription() != null && !"".equals(f.getDescription()))
				sb.append("<div class=\"description\">").append(f.getDescription()).append("</div>");
			sb.append("</div>");
		}

		sb.append("</div>");

		return sb.toString();
	}

	private String formatDefinition(List<DefinitionLight> list, RequestContext context) {
		StringBuilder sb = new StringBuilder();

		sb.append(getTabs("Definition"));

		sb.append("<div class=\"top\"><div class=\"holder\"><h1>Datahotel</h1><h2>Definitions</h2></div></div>");

		sb.append("<div class=\"holder list\">");
		int i = 0;
		for (DefinitionLight f : list) {
			i++;
			sb.append("<div class=\"").append(i % 2 == 1 ? "odd" : "even").append("\">");
			sb.append("<h3><a href=\"/api/html/_def/").append(f.getShortName()).append("\">").append(f.getName())
					.append("</a></h3>");
			if (f.getDescription() != null && !"".equals(f.getDescription()))
				sb.append("<div class=\"description\">").append(f.getDescription()).append("</div>");
			sb.append("</div>");
		}
		sb.append("</div>");

		return sb.toString();
	}

	private String getTabs(String tab) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"holder\"><ul class=\"tabs\">");
		sb.append("<li class=\"meta\"><a href=\"https://github.com/difi/datahotel\">GitHub</a></li>");
		sb.append("<li class=\"meta\"><a href=\"https://twitter.com/datahotell\">Twitter</a></li>");

		for (Tab t : tabs)
			sb.append("<li class=\"").append(t.name.equals(tab) ? "tab active" : "tab").append("\"><a href=\"")
					.append(t.location).append("\">").append(t.name).append("</a></li>");

		sb.append("</ul></div>");
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
