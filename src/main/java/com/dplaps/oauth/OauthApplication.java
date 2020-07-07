package com.dplaps.oauth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OauthApplication {
	private static Logger logger = LogManager.getLogger(OauthApplication.class);

	public static void main(String[] args) {
		
		SpringApplication.run(OauthApplication.class, args);
		logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

}
