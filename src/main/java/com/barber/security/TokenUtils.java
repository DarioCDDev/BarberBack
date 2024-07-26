package com.barber.security;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.barber.entities.Rol;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class TokenUtils {

	private static final String SECRET_KEY = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924";
	private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_592_00L;

	public static String createToken(String nombre, String email, Rol rolId) {
		System.out.println("---------------------------");
		long expiritionTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
		Date expirationDate = new Date(System.currentTimeMillis() + expiritionTime);

		Map<String, Object> extra = new HashMap<>();
		extra.put("nombre", nombre);
		extra.put("rolId", rolId);

		return Jwts.builder().setSubject(email).setExpiration(expirationDate).addClaims(extra)
				.signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).compact();
	}

	public static UsernamePasswordAuthenticationToken getAutehntication(String token) {
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()).build().parseClaimsJws(token)
					.getBody();

			String email = claims.getSubject();

			return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
		} catch (JwtException e) {
			return null;
		}
	}

}
