package com.dplaps.oauth.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.dplaps.oauth.dao.SessionDao;
import com.dplaps.oauth.model.Session;
@Repository("SessionDaoImpl")
public class SessionDaoImpl implements SessionDao {
	@Autowired
	private MongoTemplate template;

	@Override
	public Session findSession(String mobile) {
		Query query = new Query();
		query.addCriteria(Criteria.where("mobile").is(mobile));
		return (Session) template.find(query, Session.class);
	}

}
