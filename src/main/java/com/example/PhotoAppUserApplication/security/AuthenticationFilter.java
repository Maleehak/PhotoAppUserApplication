package com.example.PhotoAppUserApplication.security;

import com.example.PhotoAppUserApplication.models.request.LoginRequest;
import com.example.PhotoAppUserApplication.services.IUserService;
import com.example.PhotoAppUserApplication.shared.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private IUserService userService;
  private Environment environment;


  public AuthenticationFilter(
      IUserService userService,
      Environment environment,
      AuthenticationManager authenticationManager) {
    super(authenticationManager);
    this.environment = environment;
    this.userService = userService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws AuthenticationException {
    try {

      LoginRequest creds = new ObjectMapper().readValue(req.getInputStream(), LoginRequest.class);

      return getAuthenticationManager().authenticate(
          new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req,
      HttpServletResponse res, FilterChain chain,
      Authentication auth) throws IOException, ServletException {

    String userName = ((User)auth.getPrincipal()).getUsername();
    UserDto userDto = userService.getUserByEmail(userName);
    String tokenSecret = environment.getProperty("token.secret");
    Instant now = Instant.now();

    byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
    SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
    String token = Jwts.builder()
        .setSubject(userDto.getUserId())
        .setExpiration(Date.from(now.plusMillis(360000)))
        .setIssuedAt(Date.from(now))
        .signWith(secretKey)
        .compact();

    res.addHeader("token", token);
    res.addHeader("userID", userDto.getUserId());
  }
}
