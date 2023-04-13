package com.example.PhotoAppUserApplication.models.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequest {
  @NotNull(message = "First name must not be null")
  @Size(min = 2, message = "First name can not be less than 2 chars ")
  private String firstName;

  @NotNull(message = "Last name must not be null")
  @Size(min = 2, message = "Last name can not be less than 2 chars")
  private String lastName;

  @NotNull(message = "Password must not be null")
  @Size(min = 4, max= 6, message = "Password must be greater than 4 chars and less than 6 chars")
  private String password;

  @NotNull(message = "Email must not be null")
  @Email
  private String email;

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
