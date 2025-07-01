package com.henriquefidelis.screen_match.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.henriquefidelis.screen_match.models.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    
}
