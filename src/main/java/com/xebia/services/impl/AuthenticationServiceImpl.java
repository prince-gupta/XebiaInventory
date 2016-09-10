package com.xebia.services.impl;

import com.xebia.common.Utility;
import com.xebia.dao.PageRoleDao;
import com.xebia.dao.UserDAO;
import com.xebia.dto.AuthenticationResponse;
import com.xebia.entities.PageRole;
import com.xebia.entities.User;
import com.xebia.entities.UserRole;
import com.xebia.enums.UserRoleEnum;
import com.xebia.exception.AuthenticationException;
import com.xebia.services.IAuthenticationService;
import com.xebia.services.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    ITokenService tokenService;


    @Autowired
    UserDAO userDAO;

    @Autowired
    PageRoleDao pageRoleDao;


    @Override
    public AuthenticationResponse generateTokenForAdminPortal(String userName, String password, String ipAddr) throws AuthenticationException {
        String token;
        String encodedPass = Utility.encode(password);
        User user = userDAO.getUserByUNamePwd(userName, encodedPass);
        if (user != null) {
            List<UserRole> userRoles = user.getUserRoles();
            boolean isValidRole = false;
            for (UserRole userRole : userRoles) {
                if (userRole.getRoleName().equals(UserRoleEnum.ADMIN.getDbValue()) || userRole.getRoleName().equals(UserRoleEnum.IT.getDbValue())) {
                    isValidRole = true;
                    break;
                }
            }
            if(!isValidRole){
                return new AuthenticationResponse(userName, "INVALID-ROLE","FAILED",false);
            }
            token = tokenService.generateToken(encodedPass, userName + ipAddr);
            user.setToken(token);
            userDAO.update(user);
            return new AuthenticationResponse(userName, token, "SUCCESS", user.getChangePassword().equals("Y"));
        } else {
            return new AuthenticationResponse(userName, "UN-AUTHENTICATED", "FAILED", false);
        }
    }

    @Override
    public AuthenticationResponse generateToken(final String userName, final String password, final String ipAddr) throws AuthenticationException {
        String token;
        String encodedPass = Utility.encode(password);
        User user = userDAO.getUserByUNamePwd(userName, encodedPass);
        if (user != null) {
            token = tokenService.generateToken(encodedPass, userName + ipAddr);
            user.setToken(token);
            userDAO.update(user);
            return new AuthenticationResponse(userName, token, "SUCCESS", user.getChangePassword().equals("Y"));
        } else {
            return new AuthenticationResponse(userName, "UN-AUTHENTICATED", "FAILED", false);
        }
    }

    @Override
    public void authenticateToken(String token, String userName, String ipAddr) throws AuthenticationException {
        tokenService.validateToken(token, userName, ipAddr);
    }

    @Override
    public void validateRole(String userName, String pathToAccess) throws AuthenticationException {
        User user = userDAO.getUserByUName(userName);
        List<UserRole> userRoles = user.getUserRoles();
        boolean needToProceed = true;
        for(UserRole userRole : userRoles){
            if(userRole.getRoleName().equals(UserRoleEnum.ADMIN.getDbValue()))
            {
                needToProceed = false;
                break;
            }
        }
        if(needToProceed) {
            String path = pathToAccess.substring(pathToAccess.lastIndexOf('/'), pathToAccess.length());
            PageRole pageRole = pageRoleDao.getByPath(path);
            List<UserRole> dbUserRoleList = pageRole.getUserRoles();
            boolean havingValidRole = false;
            for (UserRole userRole : userRoles) {
                for (UserRole dbUserRole : dbUserRoleList) {
                    if (dbUserRole.getRoleName().equals(userRole.getRoleName())) {
                        havingValidRole = true;
                        break;
                    }
                }
            }
            if (!havingValidRole)
                throw new AuthenticationException("INVALID-ROLE");
        }
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
    public boolean changePassword(User user) {
        User dbUser = userDAO.getUserByUName(user.getUsername());
        dbUser.setPassword(Utility.encode(user.getPassword()));
        dbUser.setChangePassword("N");
        dbUser.setToken(null);
        userDAO.update(dbUser);
        return true;
    }

    public void logout(User user) throws AuthenticationException {
        try {
            User dbUser = userDAO.getUserByUName(user.getUsername());
            dbUser.setToken(null);
            userDAO.update(dbUser);
        } catch (Exception e) {
            new AuthenticationException(e.getMessage());
        }
    }
}
