package com.moventisusa.carpoolmatch.services;

/**
 * Created by Damian Davila
 */
public class EmailExistsException extends Exception{

    public EmailExistsException(String message) {
        super(message);
    }

}
