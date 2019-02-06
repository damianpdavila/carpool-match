package com.moventisusa.carpoolmatch.service;

/**
 * Created by Damian Davila
 */
public class EmailExistsException extends Exception{

    public EmailExistsException(String message) {
        super(message);
    }

}
