package com.dplaps.oauth.helper;

import org.springframework.util.ObjectUtils;

public class UserHelper {
	
	public boolean validateMobile(String mobile) {
		if (ObjectUtils.isEmpty(mobile)) {
//			log.error("SingUp User empty mobile no {}  ", mobile);
		}
		if (!PhoneValidator.isValid(mobile)) {
			//log.error("SingUp User invalid mobile no {}  ", mobile);
		}
		return true;
	}

}
