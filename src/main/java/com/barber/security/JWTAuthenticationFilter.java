package com.barber.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		CachedBodyHttpServletRequest cachedRequest;
		try {
			cachedRequest = new CachedBodyHttpServletRequest(request);
			System.out.println(cachedRequest);
		} catch (IOException e) {
			throw new AuthenticationServiceException("Could not read request body", e);
		}

		AuthCredentials authCredentials;
		try {
			authCredentials = new ObjectMapper().readValue(cachedRequest.getReader(), AuthCredentials.class);
		} catch (IOException e) {
			throw new AuthenticationServiceException("Could not parse authentication credentials", e);
		}

		UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
				authCredentials.getEmail(), authCredentials.getPassword(), Collections.emptyList());
		return getAuthenticationManager().authenticate(usernamePAT);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
	        Authentication authResult) throws IOException, ServletException {

	    UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
	    String token = TokenUtils.createToken(userDetails.getName(), userDetails.getUsername(), userDetails.getRol());
	    response.addHeader("Authorization", "Bearer " + token);
	    System.out.println("Generating token for user: " + userDetails.getUsername());
	    System.out.println("Token: " + token);
	    chain.doFilter(request, response); 
	}

}
