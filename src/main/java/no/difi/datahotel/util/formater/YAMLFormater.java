package no.difi.datahotel.util.formater;

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

	private DumperOptions options;
	private Representer representer;
	private Yaml yaml;

	public YAMLFormater() {
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
}
