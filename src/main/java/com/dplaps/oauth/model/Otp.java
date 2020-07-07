package com.dplaps.oauth.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("dpl_otp")
public class Otp implements Serializable {

	private static final long serialVersionUID = -4412328130826908453L;

	private String mobile;
	private String otp;
	private String email;
	private String userId;
	private String reqtype;
	private int attempts = 0;
	private String templateName;
	private String createdTime = LocalDateTime.now().toString();
	private String channelToken = UUID.randomUUID().toString();
}
