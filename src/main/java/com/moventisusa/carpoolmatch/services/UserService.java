package com.moventisusa.carpoolmatch.services;

import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.models.forms.UserForm;

/**
 * Created by Damian Davila
 */
public interface UserService {

    public User save(UserForm userForm) throws EmailExistsException;
    public User update(User user) throws EmailExistsException;
    public User findByEmail(String email);

}
