package no.difi.datahotel.util.formater;

import java.util.HashMap;
import java.util.Map;

import no.difi.datahotel.model.DefinitionLight;
import no.difi.datahotel.model.Definitions;
import no.difi.datahotel.model.FieldLight;
import no.difi.datahotel.model.Fields;
import no.difi.datahotel.model.MetadataLight;
import no.difi.datahotel.model.Result;
import no.difi.datahotel.util.FormaterInterface;
import no.difi.datahotel.util.RequestContext;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * @url http://code.google.com/p/snakeyaml/
 */
public class YAMLFormater implements FormaterInterface {

	private static DumperOptions options;
	private static Representer representer;
	private static Yaml yaml;

	{
		options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		representer = new Representer();
		representer.addClassTag(MetadataLight.class, new Tag("!metadata"));
		representer.addClassTag(Result.class, new Tag("!result"));
		representer.addClassTag(Fields.class, new Tag("!fields"));
		representer.addClassTag(FieldLight.class, new Tag("!field"));
		representer.addClassTag(Definitions.class, new Tag("!definitions"));
		representer.addClassTag(DefinitionLight.class, new Tag("!definition"));

		yaml = new Yaml(representer, options);
	}

	@Override
	public String format(Object object, RequestContext context) {
		return yaml.dump(object);
	}

	@Override
	public String format(Exception exception, RequestContext context) {
		Map<String, String> object = new HashMap<String, String>();
		// object.put("status", String.valueOf(exception.getStatus()));
		object.put("error", exception.getMessage());

		return format(object, context);
	}
}
