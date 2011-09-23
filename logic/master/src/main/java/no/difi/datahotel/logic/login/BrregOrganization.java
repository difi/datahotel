package no.difi.datahotel.logic.login;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BrregOrganization {

	private HashMap<String, String> values = new HashMap<String, String>();

	public BrregOrganization(Integer orgnr) throws IOException {
		String url = "http://w2.brreg.no/enhet/sok/detalj.jsp?orgnr=" + orgnr;
		Document doc = Jsoup.connect(url).get();

		Elements tables = doc.select("table table table table");
		Elements trs = tables.get(1).getElementsByTag("tr");
		for (Element tr : trs) {
			Elements tds = tr.getElementsByTag("p");
			if (tds.size() == 2)
				set(tds.get(0).text(), tds.get(1).text());
		}
	}

	protected void set(String key, String value) {
		key = key.substring(0, key.length() - 1);

		if (value.equals("-"))
			value = null;

		values.put(key, value);
	}

	public String get(String key) {
		return values.get(key);
	}

	public String getShort() {
		String res = get("Internettadresse");

		if (res == null)
			return null;

		if (res.startsWith("www.regjeringen.no"))
			return res.substring(res.indexOf("/") + 1);
		
		res = res.substring(0, res.lastIndexOf("."));

		if (res.startsWith("www."))
			res = res.substring(4);

		return res.replace(".", "");
	}
}
