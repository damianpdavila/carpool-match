package com.moventisusa.carpoolmatch.repositories;

import com.moventisusa.carpoolmatch.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Damian Davila
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByEmail(String email);
}
