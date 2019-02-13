package com.moventisusa.carpoolmatch.repositories;

import com.moventisusa.carpoolmatch.models.MatchCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Damian Davila
 */
public interface MatchCriteriaRepository extends JpaRepository<MatchCriteria, Integer> {}
