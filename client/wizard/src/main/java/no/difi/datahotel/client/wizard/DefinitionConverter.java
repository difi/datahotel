package no.difi.datahotel.client.wizard;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import no.difi.datahotel.logic.model.DefinitionEntity;

@FacesConverter(forClass = DefinitionEntity.class)
public class DefinitionConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		DefinitionEntity def = new DefinitionEntity();
		def.setShortName(value);

		return def;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		return ((DefinitionEntity) object).getShortName();
	}

}
