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
    public User update(User updatedUser ) throws EmailExistsException {

        /* Ensure no duplicate email since it is used as login userid */
        User existingUser = userRepository.findByEmail(updatedUser.getEmail());

        if (existingUser == null) {
            ;
        } else if (updatedUser.getUid() == existingUser.getUid()) {
            /* just an update to existing user so okay */
            ;
        } else {
            /* different user, same email so error */
            throw new EmailExistsException("The email address "
                    + updatedUser.getEmail() + " already exists in the system");
        }

        userRepository.save(updatedUser);

        return updatedUser;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
