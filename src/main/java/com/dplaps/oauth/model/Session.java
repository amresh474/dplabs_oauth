package com.dplaps.oauth.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("dpl_session")
public class Session implements Serializable {
	private String mobile;
	private Map<String, String> sessionMap;
	private Map<String, String> channelMap;
	private String email;
	private String userId;
	private String firstName;
	@JsonIgnore
	private String createdDt = LocalDateTime.now().toString();
	@JsonIgnore
	private String updatedDt = LocalDateTime.now().toString();

	private Boolean showSignUp;

	@JsonIgnore
	private String channelToken;

}
