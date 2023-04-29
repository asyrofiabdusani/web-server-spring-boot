package com.rofi.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rofi.exceptions.UserServiceException;
import com.rofi.io.entity.UserEntity;
import com.rofi.repositories.UserRepository;
import com.rofi.service.UserService;
import com.rofi.shared.dto.UserDto;
import com.rofi.shared.dto.UserUtils;
import com.rofi.ui.model.response.ErrorMessages;


@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserUtils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public UserDto createUser(UserDto user) {
		
		
//		UserEntity storedUserDetails = userRepository.findByEmail(user.getEmail());
		
		if(userRepository.findByEmail(user.getEmail()) != null) throw new RuntimeException("Record already exist");
		
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		
		String publicUserId = utils.generatedUserId(21);
		
		
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		UserEntity storedUserDetails =  userRepository.save(userEntity);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, returnValue);
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) {
			throw new UsernameNotFoundException(email);
		}
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}
	
	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) {
			throw new UsernameNotFoundException(email);
		}
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) {
			throw new UsernameNotFoundException("User with Id = " + userId + " not found");
		}
		
		BeanUtils.copyProperties(userEntity, returnValue);
		
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto user) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}
		
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		
		UserEntity updateUserDetails =  userRepository.save(userEntity);
		
		
		BeanUtils.copyProperties(updateUserDetails, returnValue);
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
			
		}
		userRepository.delete(userEntity);
		
		
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();
		
		page = page -1;
		
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> userPage =  userRepository.findAll(pageableRequest);
		List<UserEntity> users = userPage.getContent();
		
		
		for (UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
			}
		
		return returnValue;
	}
	
}
