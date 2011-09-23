package no.difi.datahotel.client.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.impl.DatasetEJB;
import no.difi.datahotel.logic.impl.DefinitionEJB;
import no.difi.datahotel.logic.model.DatasetEntity;
import no.difi.datahotel.logic.model.DefinitionEntity;
import no.difi.datahotel.logic.model.FieldEntity;

@RequestScoped
@ManagedBean(name = "definition")
public class DefinitionBean {

	private static Logger logger = Logger.getLogger(DefinitionEntity.class.getSimpleName());

	@EJB
	private DefinitionEJB definitionEJB;

	@EJB
	private DatasetEJB datasetEJB;

	private DefinitionEntity def = new DefinitionEntity();

	public List<DefinitionEntity> getAll() {
		return definitionEJB.getAll();
	}

	public String getShortName() {
		return this.def.getShortName();
	}

	public void setShortName(String shortName) {
		this.def = definitionEJB.getDefinitionByShortName(shortName);
	}

	public DefinitionEntity getDef() {
		return this.def;
	}

	public void setDef(DefinitionEntity def) {
		this.def = def;
	}

	public String save() {
		definitionEJB.save(def);

		// Oppdaterte berorte dataset

		return "pretty:def-list";
	}

	/**
	 * Henter ut alle datasett som inneholder en gitt definisjon.
	 * 
	 * @return List with DatasetEntitys
	 */
	public List<DatasetEntity> getDatasets() {
		try {
			List<DatasetEntity> all = datasetEJB.getAll();
			List<DatasetEntity> toReturn = new ArrayList<DatasetEntity>();
			for (DatasetEntity dataset : all) {
				for (FieldEntity field : dataset.getCurrentVersion().getFields()) {
					logger.info("FIELD: " + field.getName());
					if (field.getDefinition() != null && field.getDefinition().equals(def)) {
						toReturn.add(dataset);
						break;
					}
				}
			}

			return toReturn;
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		return null;
	}
}
