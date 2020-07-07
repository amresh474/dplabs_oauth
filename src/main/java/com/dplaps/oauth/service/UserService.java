package com.dplaps.oauth.service;

import com.dplaps.oauth.model.Session;
import com.dplaps.oauth.model.User;

public interface UserService {

	public User singupOrLoginUser(User user);

	public Session findUser(String mobile);

	public Session findUser(String mobile, String string);

}
