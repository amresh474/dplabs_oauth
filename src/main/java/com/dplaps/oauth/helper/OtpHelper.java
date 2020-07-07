package com.ml.sso.helper;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.ml.sso.dao.OtpDao;
import com.ml.sso.ex.SsoErrorCode;
import com.ml.sso.ex.SsoBadReqException;
import com.ml.sso.models.Otp;
import com.ml.sso.properties.CommonCfg;
import com.ml.sso.util.TotpGen;
import com.ml.sso.validator.PhoneValidator;

@Component
public class OtpHelper {

	@Autowired
	private OtpDao otpDao;

	@Autowired
	private CommonCfg commonCfg;

	@Autowired
	private TotpGen totpGen;

	private static final Logger LOGGER = LoggerFactory.getLogger(OtpHelper.class);

	private LocalDateTime validOtpDate() {
		LocalDateTime now = LocalDateTime.now();
		LOGGER.info("Current time:" + now + " :" + commonCfg.OTP_VALID_TIME_DURATION);
		return LocalDateTime.now().minusMinutes(commonCfg.OTP_VALID_TIME_DURATION);
	}

	private Otp isValid(List<Otp> otps, String channelToken) {
		Otp validOtp = null;
		if (ObjectUtils.isEmpty(otps)) {
			return validOtp;
		}
		for (Otp otp : otps) {
			if (isValid(otp, channelToken) != null) {
				return otp;
			}
		}

		return validOtp;
	}

	private Otp isValid(Otp otp, String channelToken) {
		if (ObjectUtils.isEmpty(otp)) {
			return null;
		}
		LocalDateTime validDate = validOtpDate();
		LocalDateTime otpDate = LocalDateTime.parse(otp.getCreatedTime());
		LOGGER.info(validDate + "  :  " + otpDate);

		if (validDate.isAfter(otpDate)) {
			// throw new MedlifeException(OTPErrorCodes.OTP_EXPIRED);
			return null;
		} else if (!otp.getChannelToken().equals(channelToken)) {
			return null;
		} else if (commonCfg.OTP_MAX_ATTEMPTS_ALLOWED <= otp.getAttempts()) {
			// throw new MedlifeException(OTPErrorCodes.OTP_ATTEMPT_REACHED);
			return null;
		}
		return otp;

	}

	public String genOtp(Otp otp) {
		if (!PhoneValidator.isValid(otp.getMobile().trim())) {
			throw new SsoBadReqException(SsoErrorCode.Sso_InvalidMobileNo);
		}
		List<Otp> existingOtps = otpDao.find(otp.getMobile());
		Otp validOtp = isValid(existingOtps, otp.getChannelToken());
		if (validOtp != null) {
			return validOtp.getOtp();
		}
		String otpVal = totpGen.generate(otp.getMobile(), "" + System.currentTimeMillis(), commonCfg.OTP_DIGIT);
		otp.setOtp(otpVal);
		otp.setCreatedTime(LocalDateTime.now().toString());
		otpDao.save(otp);
		return otpVal;
	}

	public Otp verifyOtp(String mobile, String otp, String channelToken) {
		if (!PhoneValidator.isValid(mobile.trim())) {
			LOGGER.error("Invalid Mobile Number {}", mobile);
			throw new SsoBadReqException(SsoErrorCode.Sso_InvalidMobileNo);
		}
		Otp existingOtp = otpDao.find(mobile, otp);
		if (existingOtp == null) {
			LOGGER.error("Otp does not exist for Mobile Number {}", mobile);
			throw new SsoBadReqException(SsoErrorCode.Sso_OTPNotExist);
		}
		if (isValid(existingOtp, channelToken) == null) {
			return null;
		}
		int attempt = existingOtp.getAttempts();
		existingOtp.setAttempts(attempt + 1);
		otpDao.save(existingOtp);

		if (existingOtp.getOtp().equals(otp)) {
			return existingOtp;
		} else {
			LOGGER.error("max Attempt for otp verify crossed for Mobile Number {}", mobile);
			throw new SsoBadReqException(SsoErrorCode.Sso_InvalidMobileNo);			
		}
	}

}
