package com.example.PhotoAppUserApplication.data;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
  UserEntity getByEmail(String email);
}
