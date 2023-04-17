package com.example.PhotoAppUserApplication.controllers;

import com.example.PhotoAppUserApplication.models.request.UserRequest;
import com.example.PhotoAppUserApplication.models.response.UserResponse;
import com.example.PhotoAppUserApplication.services.UserService;
import com.example.PhotoAppUserApplication.shared.UserDto;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

  @Autowired
  private Environment env;

  @Autowired
  UserService userService;

  @GetMapping("/status/check")
  public String status(){
    return "Working on port "+ env.getProperty("local.server.port")+ "with token" + env.getProperty("token.secrets");
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@Valid  @RequestBody UserRequest userRequest){

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    UserDto userDto = modelMapper.map(userRequest, UserDto.class);
    UserDto userResponseDto = userService.createUser(userDto);

    UserResponse response = modelMapper.map(userResponseDto, UserResponse.class);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}
