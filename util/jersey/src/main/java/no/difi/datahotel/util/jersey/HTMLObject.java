package no.difi.datahotel.util.jersey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.MetadataLight;

public class HTMLObject implements FormaterInterface {

	private static String style = "";
	private static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		style += "body, html, form { margin: 0; padding: 0; }";
		style += "th { text-align: left; }";
		style += "table { border-collapse: collapse; width: 100%; }";
		style += "tr.head { background-color: #4682B4; color: #fff; }";
		style += "tr.even { background-color: #E0DFDB; }";
		style += "td, th, input { padding: 4pt; }";
		style += "a { color: #00688B; }";
		style += "div.error { padding: 15; margin: 30pt; background-color: #B22222; color: #fff; font-weight: bold; }";
		style += "div.top { padding: 10pt; background-color: #4682B4; color: #fff; font-size: 19pt; }";
		style += "form { float: right; }";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String format(Object object, RequestContext context) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<html></head>");

		if (!"nostyle".equals(context.getCallback()))
			sb.append("<style>" + style + "</style>");

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
				}
			}
		}

		sb.append("</body></html>");

		return sb.toString();
	}

	private String formatCSVData(CSVData data, RequestContext context) {
		if (data.getEntries().size() == 0)
			return "";

		StringBuilder sb = new StringBuilder();

		sb.append("<div class=\"top\"><form action=\"/api/html/").append(context.getMetadata().getLocation())
				.append("\" method=\"get\"><input type=\"text\" name=\"query\" value=\"")
				.append(context.getQuery() == null ? "" : context.getQuery())
				.append("\" /><input type=\"submit\" value=\"Search\" /></form>")
				.append(context.getMetadata().getLocation()).append("</div>");

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

		if (context.getMetadata() != null)
			sb.append("<div class=\"top\">").append(context.getMetadata().getLocation()).append("</div>");

		sb.append("<table>");
		sb.append("<tr class=\"head\"><th>Name</th><th>Description</th><th>Url</th><th>Updated</th></tr>");
		int i = 0;
		for (MetadataLight m : list) {
			i++;
			sb.append("<tr class=\"").append(i % 2 == 1 ? "odd" : "even").append("\">");
			sb.append("<td><a href=\"/api/html/").append(m.getLocation()).append("\">").append(m.getName())
					.append("</a>").append("</td>");
			sb.append("<td>").append(m.getDescription() == null ? "-" : m.getDescription()).append("</td>");

			if (m.getUrl() == null)
				sb.append("<td>-</td>");
			else
				sb.append("<td><a href=\"").append(m.getUrl()).append("\" rel=\"nofollow\">")
						.append(niceUrl(m.getUrl())).append("</a></td>");

			sb.append("<td>").append(date.format(new Date(m.getUpdated() * 1000))).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");

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
}
