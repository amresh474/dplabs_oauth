package com.dplaps.oauth.dao;

import com.dplaps.oauth.model.Session;

public interface SessionDao  {

	public Session findSession(String mobile);

}
