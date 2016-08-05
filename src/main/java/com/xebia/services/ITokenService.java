package com.xebia.services;

import com.xebia.exception.AuthenticationException;

/**
 * 
 * @author Pgupta
 *
 */
public interface ITokenService {

	/**
	 * Method will generate token to return to client. It uses JSON Web Token .
	 * 
	 * @param privateKey
	 *            - Application private key
	 * @param publicKey
	 *            - Shared private key
	 * @return - generated token to be shared with client.
	 */
	public String generateToken(final String privateKey, final String publicKey);

	/**
	 * Mehtod will decode the encoded token using passed key and token.
	 * 
	 * @param key
	 *            - public key to decode token
	 * @param token
	 *            - encoded token.
	 * @return - decoded token.
	 */
	public String decodeToken(final String key, final String token);

	/**
	 * Method to decode and to check whether received token is valid against
	 * privateKey and publicKey
	 * 
	 * @param token
	 *            - Received Token
	 * @param userName
	 *            - UserName.
	 * @throws AuthenticationException
	 *             - If token does not passes validity criteria , Method throws
	 *             exception.
	 */
    public void validateToken(String token, String userName, String ipAddr) throws AuthenticationException ;

}
