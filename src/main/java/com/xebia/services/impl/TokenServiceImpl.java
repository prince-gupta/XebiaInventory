package com.xebia.services.impl;

import com.xebia.dao.UserDAO;
import com.xebia.entities.User;
import com.xebia.exception.AuthenticationException;
import com.xebia.services.ITokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements ITokenService {

    @Autowired
    UserDAO userDAO;

    @Override
    public String generateToken(String privateKey, String publicKey) {
        return Jwts.builder().setSubject(privateKey).signWith(SignatureAlgorithm.HS512, publicKey).compact();
    }

    @Override
    public String decodeToken(String secondaryKey, String token) {
        return Jwts.parser().setSigningKey(secondaryKey).parseClaimsJws(token).getBody().getSubject();
    }

    @Override
    public void validateToken(String token, String userName, String ipAddr) throws AuthenticationException {
        User user = userDAO.getUserByUNameTkn(userName, token);
        if(user == null){
            throw new AuthenticationException("UserNotFound!");
        }
        String password = decodeToken(userName+ipAddr, token);
        if (!password.equals(user.getPassword())) {
            throw new AuthenticationException("MalfunctionedTokenRecieved !");
        }
    }

}
