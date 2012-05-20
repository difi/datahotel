package no.difi.datahotel.util.jersey;

import java.util.List;
import java.util.Map;

import no.difi.datahotel.util.bridge.MetadataLight;

public class HTMLObject implements FormaterInterface {

	String style = "th { text-align: left; } table { border-collapse: collapse; width: 100%; }";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String format(Object object, String metadata) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<html></head>");
		sb.append("<style>" + style + "</style>");
		sb.append("</head><body><table>");

		if (object instanceof CSVData)
			sb.append(formatCSVData((CSVData) object));
		else if (object instanceof List<?>) {
			List list = (List) object;
			if (list.size() > 0) {
				if (list.get(0) instanceof MetadataLight)
					sb.append(formatMetadata((List<MetadataLight>) list));
			}
		}
		
		sb.append("</table></body></html>");

		return sb.toString();
	}

	private String formatCSVData(CSVData data) {
		if (data.getEntries().size() == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		
		String[] header = data.getEntries().get(0).keySet().toArray(new String[] {});
		sb.append("<tr>");
		for (String th : header)
			sb.append("<th>" + th + "</th>");
		sb.append("</tr>");
		
		for (Map<String, String> row : data.getEntries()) {
			sb.append("<tr>");
			for (String th : header)
				sb.append("<td>" +row.get(th) + "</td>");
			sb.append("</tr>");
		}
			
		return sb.toString();
	}
	
	private String formatMetadata(List<MetadataLight> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr><th>Name</th><th>Short</th><th>Description</th><th>Url</th><th>Location</th><th>Updated</th></tr>");
		for (MetadataLight m : list) {
			sb.append("<tr>");
			sb.append("<td>" + m.getName() + "</td>");
			sb.append("<td>" + m.getShortName() + "</td>");
			sb.append("<td>" + m.getDescription() + "</td>");
			sb.append("<td><a href=\"" + m.getUrl() + "\" rel=\"nofollow\">" + m.getUrl() + "</a></td>");
			sb.append("<td>" + m.getLocation() + "</td>");
			sb.append("<td>" + m.getUpdated() + "</td>");
			sb.append("</tr>");
		}
		
		return sb.toString();
	}
}
