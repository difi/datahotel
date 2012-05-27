package no.difi.datahotel.util.jersey;


public interface FormaterInterface {
	public String format(Object object, RequestContext context) throws Exception;
}