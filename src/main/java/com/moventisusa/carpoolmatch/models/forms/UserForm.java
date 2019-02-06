package com.moventisusa.carpoolmatch.models.forms;

import javax.validation.constraints.*;

/**
 * Created by Damian Davila
 */
public class UserForm {

    @NotBlank
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank
    @Pattern(regexp = "(\\S){2,}.*", message = "Full name must contain at least two characters")
    private String fullName;

    @NotBlank
    @Pattern(regexp = "(\\S){6,20}", message = "Password must have 6-20 non-whitespace characters")
    private String password;

    @NotBlank(message = "Passwords do not match")
    private String verifyPassword;

    public UserForm() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        checkPasswordForRegistration();
    }

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
        checkPasswordForRegistration();
    }

    private void checkPasswordForRegistration() {
        if (!getPassword().equals(verifyPassword)) {
            verifyPassword = null;
        }
    }

}

