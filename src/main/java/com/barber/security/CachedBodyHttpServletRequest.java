package com.barber.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
	private final String body;

	public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);
		StringBuilder bodyBuilder = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String line;
			while ((line = reader.readLine()) != null) {
				bodyBuilder.append(line);
			}
		}
		this.body = bodyBuilder.toString();
	}

	@Override
	public BufferedReader getReader() {
		return new BufferedReader(new StringReader(body));
	}
}