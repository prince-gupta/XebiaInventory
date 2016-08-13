package com.xebia.services;

import com.xebia.dto.UserDto;
import com.xebia.entities.User;
import com.xebia.exception.ApplicationException;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Pgupta on 07-08-2016.
 */
public interface IUserService {

    public List<UserDto> fetchAllUsers();

    public User createUser(UserDto user)throws ApplicationException;

    public void resetPassword(String username) throws ApplicationException;
}
