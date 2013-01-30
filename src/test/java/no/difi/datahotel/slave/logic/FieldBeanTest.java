package no.difi.datahotel.slave.logic;

import static org.junit.Assert.assertEquals;
import no.difi.datahotel.BaseTest;
import no.difi.datahotel.master.logic.MasterDefinitionBean;
import no.difi.datahotel.model.Definition;
import no.difi.datahotel.model.Metadata;

import org.junit.Before;
import org.junit.Test;

public class FieldBeanTest extends BaseTest {

	private FieldBean fieldBean;
	private MasterDefinitionBean definitionBean;

	@Before
	public void before() throws Exception {
		fieldBean = new FieldBean();
		definitionBean = new MasterDefinitionBean();
	}

	@Test
	public void testSimple() throws Exception {
		Definition d = new Definition();
		d.setShortName("fylkenr");
		d.setName("Fylkesnummer");
		definitionBean.setDefinition(d);

		Metadata metadata = new Metadata();
		metadata.setLocation("difi/geo/fylke");
		metadata.setUpdated(System.currentTimeMillis());

		// Update first
		fieldBean.update(metadata);
		assertEquals(2, fieldBean.getFields(metadata).size());

		// assertEquals(1, fieldEJB.getUsage("fylkenr").size());
		assertEquals(null, fieldBean.getUsage("kommuner"));

		// Update second
		fieldBean.update(metadata);
		assertEquals(2, fieldBean.getFields(metadata).size());

		// assertEquals(1, fieldEJB.getUsage("fylkenr").size());
		assertEquals(null, fieldBean.getUsage("kommuner"));

	}
}
