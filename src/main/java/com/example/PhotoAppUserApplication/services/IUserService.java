package com.example.PhotoAppUserApplication.services;

import com.example.PhotoAppUserApplication.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {

  UserDto createUser(UserDto user);
  UserDto getUserByEmail(String email);

}
