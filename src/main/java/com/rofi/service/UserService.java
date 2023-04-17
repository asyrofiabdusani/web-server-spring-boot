package com.rofi.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.rofi.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);
	UserDto getUser(String email);
}
