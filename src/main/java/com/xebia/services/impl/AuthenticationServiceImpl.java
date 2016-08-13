package com.xebia.services.impl;

import com.xebia.common.Utility;
import com.xebia.dao.UserDAO;
import com.xebia.dto.AuthenticationResponse;
import com.xebia.entities.User;
import com.xebia.exception.AuthenticationException;
import com.xebia.services.IAuthenticationService;
import com.xebia.services.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    ITokenService tokenService;


    @Autowired
    UserDAO userDAO;

    @Override
    public AuthenticationResponse generateToken(final String userName, final String password, final String ipAddr) throws AuthenticationException {
        String token ;
        String encodedPass = Utility.encode(password);
        User user = userDAO.getUserByUNamePwd(userName, encodedPass);
        if (user != null) {
            token = tokenService.generateToken(encodedPass, userName + ipAddr);
            user.setToken(token);
            userDAO.update(user);
            return new AuthenticationResponse(userName, token, "SUCCESS",user.getChangePassword().equals("Y"));
        } else {
            return new AuthenticationResponse(userName, "UN-AUTHENTICATED", "FAILED",false);
        }
    }

    @Override
    public void authenticateToken(String token, String userName, String ipAddr) throws AuthenticationException {
        tokenService.validateToken(token, userName, ipAddr);
    }

    @Override
    public User createUser(User user) {
        String enCodedPass = Utility.encode("Xebia123");
        user.setPassword(enCodedPass);
        user.setActive("Y");
        userDAO.create(user);
        return user;
    }

    @Override
    public boolean changePassword(User user){
        User dbUser = userDAO.getUserByUName(user.getUsername());
        dbUser.setPassword(Utility.encode(user.getPassword()));
        dbUser.setChangePassword("N");
        dbUser.setToken(null);
        userDAO.update(dbUser);
        return true;
    }

    public void logout(User user) throws AuthenticationException{
        try {
            User dbUser = userDAO.getUserByUName(user.getUsername());
            dbUser.setToken(null);
            userDAO.update(dbUser);
        }
        catch (Exception e){
            new AuthenticationException(e.getMessage());
        }
    }
}
