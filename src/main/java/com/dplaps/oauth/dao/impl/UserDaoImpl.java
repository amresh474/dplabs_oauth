package com.dplaps.oauth.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.dplaps.oauth.dao.UserDao;
import com.dplaps.oauth.model.Session;
import com.dplaps.oauth.model.User;

@Repository("UserDaoImpl")
public class UserDaoImpl implements UserDao {
	@Autowired
	private MongoTemplate template;

	@Override
	public User findUser(String mobile) {
		Query query = new Query();
		query.addCriteria(Criteria.where("mobile").is(mobile));
//		return (Session) template.find(query, Session.class);
		return (User) template.find(query, User.class);
	}

}
