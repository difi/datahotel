package no.difi.datahotel.logic.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.FieldEntity;
import no.difi.datahotel.logic.model.VersionEntity;
import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Fields;

/**
 * {@code XmlEJB} handles the I/O of {@code XML} files. It utilizes {@code JAXB}
 * for serialization.
 */
@Stateless
public class XmlEJB {

	@EJB
	private FieldEJB fieldEJB;

	public void saveFieldsToDisk(DatasetEntity dataset, VersionEntity version) throws Exception {
		Fields fields = new Fields();
		List<FieldEntity> fieldList = fieldEJB.getByVersion(version);
		for (int i = 0; i < fieldList.size(); i++) {
			FieldEntity fieldEntity = fieldList.get(i);
			Field field = new Field();
			field.setColumnNumber(i);

			field.setDefinition(createDefinition(fieldEntity));
			field.setGroupable(fieldEntity.getGroupable());
			field.setIndexPrimaryKey(fieldEntity.getPrimaryIndexKey());
			field.setContent(fieldEntity.getDescription());
			field.setName(fieldEntity.getName());
			field.setSearchable(fieldEntity.getSearchable());
			field.setShortName(fieldEntity.getShortName());

			fields.getFields().add(field);

		}

		fields.save(dataset.getDatasetGroup().getOwner().getShortName(), dataset.getDatasetGroup().getShortName(),
				dataset.getShortName());
	}

	private Definition createDefinition(FieldEntity fieldEntity) {
		if (fieldEntity.getDefinition() != null) {
			Definition definition = new Definition();
			definition.setDescription(fieldEntity.getDefinition().getDescription());
			definition.setName(fieldEntity.getDefinition().getName());
			definition.setShortName(fieldEntity.getDefinition().getShortName());
			return definition;
		}
		return null;
	}
}
