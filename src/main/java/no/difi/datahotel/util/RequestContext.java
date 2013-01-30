package no.difi.datahotel.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import no.difi.datahotel.model.FieldLight;

public class RequestContext {

	private int page = 1;
	private String query = null;
	private Map<String, String> lookup = new HashMap<String, String>();
	private String callback;

	public RequestContext() {

	}

	public RequestContext(UriInfo uriInfo) {
		this(uriInfo, null);
	}

	public RequestContext(UriInfo uriInfo, List<FieldLight> fields) {
		MultivaluedMap<String, String> parameters = uriInfo.getQueryParameters();

		if (fields != null)
			for (FieldLight f : fields)
				if (f.getGroupable())
					if (parameters.containsKey(f.getShortName()))
						if (!"".equals(parameters.getFirst(f.getShortName())))
							lookup.put(f.getShortName(), parameters.getFirst(f.getShortName()));

		if (parameters.containsKey("query"))
			if (!"".equals(parameters.getFirst("query")))
				query = parameters.getFirst("query");

		if (parameters.containsKey("callback"))
			if (!"".equals(parameters.getFirst("callback")))
				callback = parameters.getFirst("callback");

		if (parameters.containsKey("page"))
			if (!"".equals(parameters.getFirst("page")))
				page = Integer.parseInt(parameters.getFirst("page"));
	}

	public int getPage() {
		return page;
	}

	public String getQuery() {
		return query;
	}

	public Map<String, String> getLookup() {
		return lookup;
	}

	public String getCallback() {
		return callback;
	}

	@Deprecated
	public void setCallback(String callback) {
		this.callback = callback;
	}

	public boolean isSearch() {
		return query != null || lookup.size() > 0;
	}
}
