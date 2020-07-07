package com.dplaps.oauth.validator;


import org.apache.commons.lang3.StringUtils;

public class PhoneValidator {
	public static boolean isValid(String phoneNumber) {
		if (StringUtils.isBlank(phoneNumber)) {
			return false;
		}

		if (phoneNumber.matches("[0-9]{10}")) {
			return true;
		}

		// validating phone number with -, . or spaces
		if (phoneNumber.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) {
			return true;
		}
		// validating phone number with extension length from 3 to 5
		else if (phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) {
			return true;
		}
		// validating phone number where area code is in braces ()
		else if (phoneNumber.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) {
			return true;
		}
		// return false if nothing matches the input
		else {
			return false;
		}
	}

}
