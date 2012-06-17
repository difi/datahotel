package no.difi.datahotel.util.formater;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import no.difi.datahotel.model.Result;
import no.difi.datahotel.util.FormaterInterface;
import no.difi.datahotel.util.RequestContext;
import no.difi.datahotel.util.csv.CSVWriter;

/**
 * Class for formatting objects into representable CSV data.
 */
public class CSVFormater implements FormaterInterface {

	public String format(Object object, RequestContext context) throws Exception {
		if (object instanceof Result) {
			List<Map<String, String>> data = ((Result) object).getEntries();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CSVWriter writer = new CSVWriter(baos);

			writer.writeHeader(data.get(0).keySet().toArray(new String[0]));
			for (Map<String, String> row : data)
				writer.write(row.values().toArray(new String[0]));

			writer.close();

			return baos.toString("UTF-8");
		}

		throw new Exception("Unable to parse content.");
	}
}
