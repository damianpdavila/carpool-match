package com.moventisusa.carpoolmatch.models;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MatchedUser {

    private User user;

    private Integer matchTotal;

    private LinkedHashMap<String, Integer> matchDetail;

    public MatchedUser(User user) {
        this.user = user;
        this.matchTotal = 0;
        this.matchDetail = new LinkedHashMap<>();
    }

    public void addMatch(String criterium, Integer matchValue) {
        this.matchTotal += matchValue;
        this.matchDetail.put(criterium, matchValue);
    }

    public User getUser() {
        return user;
    }

    public Integer getMatchTotal() {
        return matchTotal;
    }

    public Map<String, Integer> getMatchDetail() {
        return matchDetail;
    }

    @Override
    public String toString() {
        return "MatchedUser{" +
                "user=" + user +
                ", matchTotal=" + matchTotal +
                ", matchDetail=" + matchDetail +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchedUser)) return false;
        MatchedUser that = (MatchedUser) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(matchTotal, that.matchTotal) &&
                Objects.equals(matchDetail, that.matchDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, matchTotal, matchDetail);
    }
}
