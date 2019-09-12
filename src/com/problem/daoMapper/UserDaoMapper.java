package com.problem.daoMapper;

import java.util.List;

import com.problem.entity.User;

public interface UserDaoMapper {
	User  findByUserNameAndPassword( User user);
	
	List<User> findAll();
}
