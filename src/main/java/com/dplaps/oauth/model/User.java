package com.dplaps.oauth.model;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("dpl_user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String mobile;
	private String firstName;
	private String lastName;
	private String user;
	private Map<String, String> userMap;

	private String email;
	private Map<String, String> emailMap;
	private String gender;
	private Date dob;
	private boolean locked;

	private String createdbycat = LocalDateTime.now().toString();

	private String updatedD = LocalDateTime.now().toString();
	@JsonIgnore
	private String sysSources;
	@JsonIgnore
	private String channelToken;
	@JsonIgnore
	private String sysSessionToken;

}
