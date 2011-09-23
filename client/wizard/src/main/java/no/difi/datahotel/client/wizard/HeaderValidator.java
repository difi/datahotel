package no.difi.datahotel.client.wizard;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import no.difi.datahotel.logic.model.FieldDTO;

@FacesValidator(value = "headerValidator")
public class HeaderValidator implements Validator {

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object value) throws ValidatorException {
        List<FieldDTO> fields=null;

        String result = processValidationResults(fields, (String) value);
        if (!result.equals("")) {
            FacesMessage message = new FacesMessage();
            message.setDetail(result);
            message.setSummary(result);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public boolean checkforNoneUniquHeaders(List<FieldDTO> fields) {
        for (int i = 0; i < fields.size(); i++) {
            /*
             * Checks for equal headers.
             */
            for (int j = 0; j < fields.size(); j++) {
                if (i == j) {
                    continue;
                }
                if (fields.get(i).getField().equalsIgnoreCase(fields.get(j).getField())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkFieldsForIllegalChars(String field) {
        if (field.matches("[a-z0-9_]")) {
            return false;
        }
        return true;
    }

    public boolean fieldHasCorrectSize(String field) {
        if (field.length() < 3 && field.length() > 20) {
            return false;
        }
        return true;
    }

    public String processValidationResults(List<FieldDTO> fields, String value) {
        String returnMessage = "";

        if (!fieldHasCorrectSize(value)) {
            returnMessage += "Kortnavn må ha mellom 3 og 20 teikn.";
        }
        if (!checkFieldsForIllegalChars(value)) {
            returnMessage += "Teksten inneheld ulovlege teikn";
        }
//        if (!checkforNoneUniquHeaders(fields)) {
//            returnMessage += "Same kolonneoverskrift er nytte fleire gonger";
//        }
        return returnMessage;
    }
}
