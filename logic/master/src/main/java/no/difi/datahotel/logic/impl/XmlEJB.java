package no.difi.datahotel.logic.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.FieldEntity;
import no.difi.datahotel.logic.model.GroupEntity;
import no.difi.datahotel.logic.model.OwnerEntity;
import no.difi.datahotel.logic.model.VersionEntity;
import no.difi.datahotel.util.bridge.Definition;
import no.difi.datahotel.util.bridge.Field;
import no.difi.datahotel.util.bridge.Fields;
import no.difi.datahotel.util.bridge.Structure;

/**
 * {@code XmlEJB} handles the I/O of {@code XML} files. It utilizes {@code JAXB}
 * for serialization.
 */
@Stateless
public class XmlEJB {

	@EJB
	private OwnerEJB ownerEJB;
	@EJB
	private GroupEJB groupEJB;
	@EJB
	private DatasetEJB datasetEJB;
	
	@EJB
	private FieldEJB fieldEJB;

	/**
	 * Generates a {@code XML} structure describing all {@code datasets} and
	 * saves it to disc.
	 * <p>
	 * The structure is a tree view of all {@code Owners} ->
	 * {@code DatasetGroups} -> {@code Datasets} </br> It will be located in the
	 * base {@code datahotel} folder, and named {@code datasetStructure.xml}
	 * 
	 * @return the generated structure.
	 * @see #readDatasetStructure()
	 * @see Structure
	 */
	public void saveDatasetStructureToDisk() throws Exception {
		Structure datasetStructure = new Structure();
		Map<String, Map<String, Map<String, Long>>> structure = new HashMap<String, Map<String, Map<String, Long>>>();
		datasetStructure.setStructure(structure);

		for (OwnerEntity o : ownerEJB.getAll()) {
			Map<String, Map<String, Long>> groups = new HashMap<String, Map<String, Long>>();

			for (GroupEntity g : groupEJB.getByOwner(o)) {
				Map<String, Long> datasets = new HashMap<String, Long>();

				for (DatasetEntity d : datasetEJB.getDatasetsByDatasetGroup(g))
					if (d.getLastEdited() != 0)
						datasets.put(d.getShortName(), d.getLastEdited());

				if (datasets.size() != 0)
					groups.put(g.getShortName(), datasets);
			}
			
			if (groups.size() != 0)
				structure.put(o.getShortName(), groups);
		}

		datasetStructure.save();
	}

	private Definition createDefinition(FieldEntity fieldEntity) {
		if (fieldEntity.getDefinition() != null) {
			Definition definition = new Definition();
			definition.setDescription(fieldEntity.getDefinition()
					.getDescription());
			definition.setName(fieldEntity.getDefinition().getName());
			definition.setShortName(fieldEntity.getDefinition().getShortName());
			return definition;
		}
		return null;
	}

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

		fields.save(dataset.getDatasetGroup().getOwner().getShortName(),
				dataset.getDatasetGroup().getShortName(),
				dataset.getShortName());
	}
}
