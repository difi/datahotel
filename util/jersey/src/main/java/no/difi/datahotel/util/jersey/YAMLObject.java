package no.difi.datahotel.util.jersey;


import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * @url http://code.google.com/p/snakeyaml/
 */
public class YAMLObject implements FormaterInterface {

	private static DumperOptions options;

	{
		options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	}

	public String format(Object object, RequestContext context) {
		return new Yaml(options).dump(object);
	}
}
