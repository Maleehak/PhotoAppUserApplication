package com.example.PhotoAppUserApplication.security;
import com.example.PhotoAppUserApplication.services.IUserService;
import com.example.PhotoAppUserApplication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {

  BCryptPasswordEncoder bCryptPasswordEncoder;
  IUserService userService;
  Environment environment;

  public WebSecurity(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService, Environment environment) {
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userService = userService;
    this.environment = environment;
  }


  @Bean
  protected SecurityFilterChain configure(HttpSecurity http) throws Exception{
    // Configure AuthenticationManagerBuilder
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

    // Create AuthenticationFilter
    AuthenticationFilter authenticationFilter =
        new AuthenticationFilter(userService, environment, authenticationManager);
    authenticationFilter.setFilterProcessesUrl("/users/login");

    http.csrf().disable();

    http.authorizeHttpRequests()
        .requestMatchers(HttpMethod.POST, "/users").permitAll()
        .requestMatchers(HttpMethod.GET, "/users/status/check").permitAll()
        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
        .and()
        .addFilter(authenticationFilter)
        .authenticationManager(authenticationManager)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.headers().frameOptions().disable();

    return http.build();

  }
}
