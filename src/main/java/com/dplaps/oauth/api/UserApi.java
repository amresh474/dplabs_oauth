package com.dplaps.oauth.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dplaps.oauth.model.Session;
import com.dplaps.oauth.model.User;
import com.dplaps.oauth.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping

public class UserApi {
	private static Logger logger = LogManager.getLogger(UserApi.class);
	@Autowired
	private UserService userService;
//	@Value("${api.token}")
	private String xsToken;

	@GetMapping(path = "user/{mobile}")
	public Session getUserProfile(@PathVariable String mobile, @RequestHeader HttpHeaders reqHeaders) {
		if (!ObjectUtils.isEmpty(reqHeaders.get("x-secret"))) {
			if (xsToken.equals(reqHeaders.get("x-secret").get(0))) {
				return userService.findUser(mobile);
			} 
		}
		else if (!ObjectUtils.isEmpty(reqHeaders.get("x-s-id"))) {
				return userService.findUser(mobile, reqHeaders.get("x-s-id").get(0));
			}

		return null;
	}

	@PostMapping(path = "user")
	public ResponseEntity<?> singupOrLoginUser(@RequestBody User user, @RequestHeader HttpHeaders reqHeaders) {
		if (!ObjectUtils.isEmpty(reqHeaders.get("x-s-id"))) {
			user.setChannelToken(reqHeaders.get("x-s-id").get(0));
		}
		User respUser = userService.singupOrLoginUser(user);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.set("x-s-id", respUser.getChannelToken());
		return ResponseEntity.status(HttpStatus.CREATED).headers(respHeaders).body("");
	}

}
