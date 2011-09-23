package no.difi.datahotel.util.jsf;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;

/**
 * Contains validity checks of {@code JPA Entity} fields
 */
@Stateless
public class Validation {
	
	// Variables used for Short Name validation
	public static final int SHORT_NAME_MIN_LENGTH = 3;
	public static final int SHORT_NAME_MAX_LENGTH = 20;
	public static final String SHORT_NAME_PATTERN = "[a-zæøå0-9_-]{"+(SHORT_NAME_MIN_LENGTH)+","+(SHORT_NAME_MAX_LENGTH)+"}";
	
	/**
	 * Checks the validity of a {@code shortName}. It may contain characters a-å, A-Å, 0-9 '-' and '_'. 
	 * @param shortName - the {@code String} to validate
	 * @return - {@code Set<String>} if the string is valid.
	 */
	public static Set<String> validateShortname(String shortName){
		Set<String> messages = new HashSet<String>();
		
		if (!shortName.matches(SHORT_NAME_PATTERN)) {
			if (shortName.contains(" ")) {
				messages.add("Korte navn kan ikke inneholde mellomrom.");
			}
			if (shortName.length() > SHORT_NAME_MAX_LENGTH) {
				messages.add("Korte navn kan ikke være lengre enn "+SHORT_NAME_MAX_LENGTH+".");
			}
			else if (shortName.length() < SHORT_NAME_MIN_LENGTH) {
				messages.add("Korte navn kan ikke være kortere enn "+SHORT_NAME_MIN_LENGTH+".");
			}
			else {
				messages.add("Inneholder ulovlige tegn.");
			}
			
		}
		
		return messages;
	}

	
	
}
