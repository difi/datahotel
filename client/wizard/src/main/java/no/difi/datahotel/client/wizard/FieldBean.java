package no.difi.datahotel.client.wizard;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import no.difi.datahotel.logic.impl.DefinitionEJB;
import no.difi.datahotel.logic.impl.FieldEJB;
import no.difi.datahotel.logic.model.DefinitionEntity;
import no.difi.datahotel.logic.model.FieldEntity;

@ManagedBean(name = "field")
@RequestScoped
public class FieldBean {

	@EJB
	private FieldEJB fieldEJB;

	@EJB
	private DefinitionEJB definitionEJB;

	@ManagedProperty(value = "#{version}")
	private VersionBean versionBean;

	private Integer fieldIndex = 0;
	private FieldEntity field;

	public List<FieldEntity> getAll() {
		return fieldEJB.getByVersion(versionBean.getVersion());
	}

	public List<DefinitionEntity> getDefinitions() {
		return definitionEJB.getAll();
	}

	public void setVersionBean(VersionBean versionBean) {
		this.versionBean = versionBean;
	}

	public FieldEntity getField() {
		return field;
	}

	public void setField(FieldEntity field) {
		this.field = field;
	}

	public void setFieldIndex(Integer fieldIndex) {
		this.fieldIndex = fieldIndex;
		this.field = getAll().get(fieldIndex);
	}

	public Integer getFieldIndex() {
		return this.fieldIndex;
	}

	public String start(boolean headers) throws Exception {
		if (getAll().size() == 0) {
			String[] header = versionBean.getTop().get(0);

			for (int i = 0; i < header.length; i++) {
				FieldEntity field = new FieldEntity();
				field.setVersion(versionBean.getVersion());
				field.setName(headers ? header[i] : "");
				field.setShortName(headers ? header[i].toLowerCase() : "");
				fieldEJB.save(field);
			}
		}

		return "pretty:manage_field";
	}

	public String next() {
		return moveTo(fieldIndex + 1);
	}
	
	public String moveTo(int fieldIndex) {
		if (field != null) {
			field.setDefinition(definitionEJB.getDefinitionByShortName(field.getDefinition().getShortName()));
			field.setEdited(true);
			fieldEJB.save(field);
		}
		
		this.fieldIndex = fieldIndex;
		
		if (fieldIndex == -1)
			return "pretty:manage_version";
		if (getAll().size() <= fieldIndex)
			return "pretty:manage_field_publish";
		return "pretty:manage_field";
	}
}
