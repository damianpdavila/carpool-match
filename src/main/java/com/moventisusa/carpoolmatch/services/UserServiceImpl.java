package com.moventisusa.carpoolmatch.services;

import com.moventisusa.carpoolmatch.models.MatchCriteria;
import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.models.forms.UserForm;
import com.moventisusa.carpoolmatch.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Created by Damian Davila
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public User save(UserForm userForm) throws EmailExistsException {

        User existingUser = userRepository.findByEmail(userForm.getEmail());
        if (existingUser != null)
            throw new EmailExistsException("The email address "
                    + userForm.getEmail() + " already exists in the system");

        User newUser = new User(
                userForm.getEmail(),
                userForm.getFullName(),
                passwordEncoder.encode(userForm.getPassword()));
        userRepository.save(newUser);

        return newUser;
    }

    @Transactional
    @Override
    public User update(User updatedUser) throws EmailExistsException {

        /* If email changed, ensure not exists already */
        User existingUser = userRepository.findById(updatedUser.getUid()).get();
        if ( ! updatedUser.getEmail().equals(existingUser.getEmail())){

            /* email changed must validate it */
            if (userRepository.findByEmail(updatedUser.getEmail()) != null){
                throw new EmailExistsException("The email address "
                        + updatedUser.getEmail() + " already exists in the system");
            }
        }
        userRepository.save(updatedUser);

        return updatedUser;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
