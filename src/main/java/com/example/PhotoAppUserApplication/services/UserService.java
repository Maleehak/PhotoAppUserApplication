package com.example.PhotoAppUserApplication.services;

import com.example.PhotoAppUserApplication.data.UserEntity;
import com.example.PhotoAppUserApplication.data.UserRepository;
import com.example.PhotoAppUserApplication.shared.UserDto;
import java.util.ArrayList;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService{

  UserRepository userRepository;
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Override
  public UserDto createUser(UserDto user) {
    user.setUserId(UUID.randomUUID().toString());

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    UserEntity userEntity= modelMapper.map(user, UserEntity.class);
    userEntity.setEncryptedPassword(
        bCryptPasswordEncoder.encode(user.getPassword()));

    userRepository.save(userEntity);

    UserDto userDto = modelMapper.map(userEntity, UserDto.class);
    return userDto;
  }

  @Override
  public UserDto getUserByEmail(String email) throws UsernameNotFoundException {
    UserEntity userEntity = userRepository.getByEmail(email);
    if(userEntity == null) throw new UsernameNotFoundException(email);

    return new ModelMapper().map(userEntity, UserDto.class);

  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity userEntity = userRepository.getByEmail(username);
    if(userEntity == null) throw  new UsernameNotFoundException(username);

    return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
        true, true, true, true, new ArrayList<>());
  }
}
