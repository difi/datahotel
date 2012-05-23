package no.difi.datahotel.util.jersey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.MetadataLight;

public class HTMLObject implements FormaterInterface {

	String style = "body, html { margin: 0; padding: 0; } th { text-align: left; } table { border-collapse: collapse; width: 100%; } tr.head { background-color: #4682B4; color: #fff; } tr.even { background-color: #E0DFDB; } td, th { padding: 3pt; } a { color: #00688B; } div.error { padding: 15; margin: 30pt; background-color: #B22222; color: #fff; font-weight: bold; }";
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String format(Object object, String metadata, List<Field> fields) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<html></head>");

		if (!"nostyle".equals(metadata))
			sb.append("<style>" + style + "</style>");

		sb.append("</head><body>");

		if (object instanceof Map) {
			Map<String, String> values = (Map<String, String>) object;
			for (String key : values.keySet())
				sb.append("<div class=\"").append(key).append("\">").append(values.get(key)).append("</div>");
		} else {
			sb.append("<table>");

			if (object instanceof CSVData) {
				sb.append(formatCSVData((CSVData) object, fields));
			} else if (object instanceof List<?>) {
				List list = (List) object;
				if (list.size() > 0) {
					if (list.get(0) instanceof MetadataLight)
						sb.append(formatMetadata((List<MetadataLight>) list));
				}
			}

			sb.append("</table>");
		}

		sb.append("</body></html>");

		return sb.toString();
	}

	private String formatCSVData(CSVData data, List<Field> fields) {
		if (data.getEntries().size() == 0)
			return "";

		StringBuilder sb = new StringBuilder();

		sb.append("<tr class=\"head\">");
		for (Field field : fields)
			sb.append("<th class=\"").append(field.getShortName()).append("\"><span title=\"")
					.append(field.getContent()).append("\">").append(field.getName()).append("</span></th>");
		sb.append("</tr>");

		int i = 0;
		for (Map<String, String> row : data.getEntries()) {
			i++;
			sb.append("<tr class=\"").append(i % 2 == 1 ? "odd" : "even").append("\">");
			for (Field field : fields)
				sb.append("<td class=\"").append(field.getShortName()).append("\">")
						.append(row.get(field.getShortName())).append("</td>");
			sb.append("</tr>");
		}

		return sb.toString();
	}

	private String formatMetadata(List<MetadataLight> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr class=\"head\"><th>Name (Short)</th><th>Description</th><th>Url</th><th>Updated</th></tr>");
		int i = 0;
		for (MetadataLight m : list) {
			i++;
			sb.append("<tr class=\"").append(i % 2 == 1 ? "odd" : "even").append("\">");
			sb.append("<td><a href=\"/api/html/").append(m.getLocation()).append("\">").append(m.getName())
					.append("</a> (").append(m.getShortName()).append(")</td>");
			sb.append("<td>").append(m.getDescription() == null ? "-" : m.getDescription()).append("</td>");
			sb.append("<td><a href=\"").append(m.getUrl()).append("\" rel=\"nofollow\">").append(m.getUrl())
					.append("</a></td>");
			sb.append("<td>").append(date.format(new Date(m.getUpdated() * 1000))).append("</td>");
			sb.append("</tr>");
		}

		return sb.toString();
	}
}
