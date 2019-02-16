package com.moventisusa.carpoolmatch.comparators;

import com.moventisusa.carpoolmatch.models.MatchedUser;

import java.util.Comparator;

public class MatchValueComparator implements Comparator<MatchedUser> {

    @Override
    public int compare(MatchedUser o1, MatchedUser o2) {
        /* sort descending by default */
        return o2.getMatchTotal().compareTo(o1.getMatchTotal());
    }
}
