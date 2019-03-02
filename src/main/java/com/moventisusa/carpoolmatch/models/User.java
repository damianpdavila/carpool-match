package com.moventisusa.carpoolmatch.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Damian Davila
 */
@Entity
public class User extends AbstractEntity {

    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    private String password;

    private String username;

    private String address1;

    private String address2;

    private String city;

    private String state;

    private String postalCode;

    private double latitude;

    private double longitude;

    @NotNull
    private Boolean enabled = true;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
    private MatchCriteria matchCriteria;

    public User() {}

    public User(@NotBlank String email, @NotBlank String fullName, @NotBlank String password) {

        if (email == null || email.length() == 0 || !isValidEmail(email))
            throw new IllegalArgumentException("Email may not be blank");

        if (fullName == null || fullName.length() == 0)
            throw new IllegalArgumentException("fullName may not be blank");

        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Password may not be blank");

        this.email = email;
        this.fullName = fullName;
        this.password = password;
    }

    public List<String> getRoles() {
        ArrayList<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        return roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.length() == 0 || !isValidEmail(email))
            throw new IllegalArgumentException("Email may not be blank");

        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public MatchCriteria getMatchCriteria() {
        return matchCriteria;
    }

    public void setMatchCriteria(MatchCriteria matchCriteria) {
        this.matchCriteria = matchCriteria;
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User user = (User) obj;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + getUid() + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", matchCriteria='" + matchCriteria + '\'' +
                '}';
    }

    private static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("\\S+@\\S+");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
