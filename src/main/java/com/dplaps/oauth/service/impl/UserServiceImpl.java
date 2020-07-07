package com.dplaps.oauth.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.dplaps.oauth.dao.SessionDao;
import com.dplaps.oauth.dao.UserDao;
import com.dplaps.oauth.enums.ReqType;
import com.dplaps.oauth.helper.UserHelper;
import com.dplaps.oauth.model.Otp;
import com.dplaps.oauth.model.Session;
import com.dplaps.oauth.model.User;
import com.dplaps.oauth.service.UserService;
import com.dplaps.oauth.validator.PhoneValidator;

@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserHelper userHelper;

	@Autowired
	private SessionDao sessionDao;

	public boolean validateMobile(String mobile) {
		if (ObjectUtils.isEmpty(mobile)) {
//			log.error("SingUp User empty mobile no {}  ", mobile);
		}
		if (!PhoneValidator.isValid(mobile)) {
//			log.error("SingUp User invalid mobile no {}  ", mobile);
		}
		return true;
	}
	
	
	private Otp userToOtpTrasform(User user, Otp otp) {
		if (ObjectUtils.isEmpty(otp)) {
			otp = new Otp();
		}
		// create Otp Object and Save It
		otp.setMobile(user.getMobile());
		otp.setEmail(user.getEmail());
		if (otp.getReqtype() == null) {
			otp.setReqtype(ReqType.Login.toString());
		}
		otp.setTemplateName("customer_otp_verification");

		String channelToken = user.getChannelToken() == null ? UUID.randomUUID().toString() : user.getChannelToken();
		user.setChannelToken(channelToken);
		otp.setChannelToken(channelToken);
		String otpVal = otpHelper.genOtp(otp);
//		log.info("userToOtpTrasform  otp :{}  for mobile :{} ", otpVal, user.getMobile());
		// System.out.println("Otp is :-" + otpVal);
		// send sms
		SMS smsNotification = GoRxSms.getOTPSMS(user.getMobile(), otpVal, "customer_otp_verification");
		// sendSms(smsNotification);
		notificationClient.sendSms(smsNotification);
		return otp;

	}


	// Get User profile
	@Override
	public Session findUser(String mobile) {
		User user = userDao.findUser(mobile);
		if (ObjectUtils.isEmpty(user)) {
			// log.error("findUser User does not exist for mobile based on x-secret " +
			// mobile);
			return null;
		}
		Session session = sessionDao.findSession(mobile);
		return null;
	}

	@Override
	public Session findUser(String mobile, String channelToken) {
//		log.info("findUser  for mobile {}  and  channelToken {}  ", mobile, channelToken);
		Session session = sessionDao.findSession(mobile);
		if (ObjectUtils.isEmpty(session)) {
			// log.error("findUser Session does not exist for mobile {} ", mobile);
		}
		Map<String, String> channelMap = session.getChannelMap();
		if (ObjectUtils.isEmpty(channelMap)) {
//			log.error("findUser Session does not exist for mobile {} and  channelMap {} ", mobile, channelToken);
		}
		if (channelMap.get(channelToken) == null) {
//			log.error("findUser Session does not exist for mobile {} and  channelToken {} ", mobile, channelToken);
		}
		User user = userDao.findUser(mobile);
		if (ObjectUtils.isEmpty(user)) {
//			log.error("findUser User does not exist for mobile based on channelToken " + mobile);
		}
		session.setChannelMap(null);
		return session;
	}

	@Override
	public User singupOrLoginUser(User user) {
		// step 1: Check Whether user exist or not
		User dbUser = userDao.findUser(user.getMobile());
		boolean isUserExist = !ObjectUtils.isEmpty(dbUser);
		// step 2A:if user does not exist go for for SignUp
		// step 2AA:if user does exist but not verified go for Otp gen for verification
		if (isUserExist && dbUser.isLocked()) {
//					log.info("singupOrLoginUser  Singup Locked for user {}  ", user);
			Otp otp = new Otp();
			otp.setReqtype(ReqType.SignUp.toString());
			dbUser.setChannelToken(user.getChannelToken());
			userToOtpTrasform(dbUser, otp);
			return dbUser;
		}
		// step 2AB:if user does not exist verify required parameter and then otp
		// generation
		/*
		 * else if (!isUserExist) { // verify the user if
		 * (!userHelper.validateMobile(user.getMobile())) {
		 * log.error("singupOrLoginUser  validation failed  for Mobile: {}  ",
		 * user.getMobile()); throw new
		 * SsoBadReqException(SsoErrorCode.Sso_validationError); } User getProfileUser =
		 * null; // call all the system to find Whether mobile no and data exist in any
		 * system or // not try { getProfileUser =
		 * asyncCallServiceImpl.executeAsyncGetprofile(user.getMobile()); } catch
		 * (JsonProcessingException | ExecutionException | InterruptedException e) {
		 * log.error("singupOrLoginUser  invalid response from system user {} , e: {}",
		 * user, e); // e.printStackTrace(); } if (!ObjectUtils.isEmpty(getProfileUser))
		 * { user = getProfileUser; } // if everything is validated // call signup here
		 * for all system userHelper.prepareSaveUser(user); // try { // List<User> users
		 * = executeAsyncSignUp(user); // for (User u : users) { //
		 * userHelper.updateSaveUser(u, user); // } // } catch (JsonProcessingException
		 * | ExecutionException | InterruptedException e) { //
		 * log.error("singupOrLoginUser  invalid response from system user {} , e: {}",
		 * user, e); // // e.printStackTrace(); // } // save user in User Db
		 * userDao.save(user); kafkaProducer.pushSignUpTopic(user); // gen Otp and send
		 * it Otp otp = new Otp(); otp.setReqtype(ReqType.SignUp.toString());
		 * userToOtpTrasform(user, otp); return user; }
		 */

		
		// step 2B:if user does exist go for for login
				// check if session exist or not
				Session dbSession = sessionDao.findSession(user.getMobile());
				dbUser.setChannelToken(user.getChannelToken());
				
				if (ObjectUtils.isEmpty(dbSession)) {
//					log.info("singupOrLoginUser  session does not exist for user {} ", user);
					userToOtpTrasform(dbUser, null);
					return dbUser;
				}
				// if channel token already exist ,user is already logged in
				Map<String, String> channelMap = dbSession.getChannelMap();
				if (!ObjectUtils.isEmpty(channelMap) && channelMap.get(user.getChannelToken()) != null) {
//					log.info("singupOrLoginUser  session already exist for user {} ", user);
					return dbUser;
				}
				userToOtpTrasform(dbUser, null);
				return dbUser;
	}
}
