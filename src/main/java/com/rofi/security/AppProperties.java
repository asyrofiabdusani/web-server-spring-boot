package com.rofi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {
	@Autowired
	private Environment env; //can call value in the application properties
	
	public String getTokenSecret() {
		return env.getProperty("tokenSecret");
	}
}
