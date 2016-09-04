package com.xebia.services;


import com.xebia.dto.AuthenticationResponse;
import com.xebia.entities.User;
import com.xebia.exception.AuthenticationException;

/**
 * @author Pgupta
 */
public interface IAuthenticationService {


    public AuthenticationResponse generateTokenForAdminPortal(final String userName, final String password, final String ipAddr) throws AuthenticationException;

    public AuthenticationResponse generateToken(final String userName, final String password, final String ipAddr) throws AuthenticationException;

    /**
     * Method will validate token passed for subsequent requests.
     *
     * @param token - token which need to be validate.
     */
    public void authenticateToken(final String token, final String publicKey, String ipAddr) throws AuthenticationException;

    public User createUser(User user);

    public boolean changePassword(User user);

    public void logout(User user);
}
