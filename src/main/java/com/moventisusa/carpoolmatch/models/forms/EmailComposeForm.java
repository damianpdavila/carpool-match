package com.moventisusa.carpoolmatch.models.forms;

import com.moventisusa.carpoolmatch.models.User;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by Damian Davila
 */
public class EmailComposeForm {

    @NotNull
    private User fromUser;

    @NotNull
    private User toUser;

    @NotBlank(message = "The message cannot be empty.")
    @Min(value = 1, message = "The message cannot be empty.")
    private String message;

    public EmailComposeForm() {
    }

    public EmailComposeForm(User fromUser, User toUser, @NotBlank(message = "The message cannot be empty.") @Min(value = 1, message = "The message cannot be empty.") String message) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
