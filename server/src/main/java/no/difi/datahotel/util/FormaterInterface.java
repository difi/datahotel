package no.difi.datahotel.util;



public interface FormaterInterface {
	public String format(Object object, RequestContext context) throws Exception;
	public String format(Exception exception, RequestContext context);
}