package com.xebia.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.xebia.annotations.Secured;
import com.xebia.common.Utility;
import com.xebia.exception.AuthenticationException;
import com.xebia.services.IAuthenticationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * Filter to intercept every call to server to validate it is Secured or not.
 * If secured then need to check for Token and Public Key which must pass in RequestHeader.
 */

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
@Component
public class AuthenticationRequestFilter implements ContainerRequestFilter {

    Logger log = Logger.getLogger(AuthenticationRequestFilter.class);

    private final String USERNAME = "Username";
    private final String CONTAINER = "Container ";

    @Autowired
    IAuthenticationService authenticationService;

    @Context
    private HttpServletRequest httpServletRequest;

    /**
     * Method to consume request to extract Header for Authorization and Username.
     *
     * @param context - ContainerRequestContext
     * @throws IOException - In case of any error occures , context abort the application
     *                          and send UNATHORIZED status.
     */
    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        String authorizationHeaderValue = context.getHeaderString(HttpHeaders.AUTHORIZATION);
        String userName = context.getHeaderString(USERNAME);
        String ip = httpServletRequest.getRemoteAddr();
        log.info("in Filter()");
        log.info("AuthorizationHeaderValue"+authorizationHeaderValue);
        log.info("UserName:" + userName);
        if (authorizationHeaderValue == null) {
            throw new NotAuthorizedException("Authorization Exception : Header is not valid.");
        }

        log.info("Token "+authorizationHeaderValue);
        try {
            validateToken(authorizationHeaderValue, userName, ip);
            Utility.put("username",userName);
        } catch (Exception e) {
            context.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    /**
     * Method to validate token against username.
     *
     * @param token - Token that need to be validated.
     * @param userName - UserName.
     */
    private void validateToken(final String token, final String userName, final String ipAddr) throws AuthenticationException{
        authenticationService.authenticateToken(token, userName, ipAddr);
    }
}

