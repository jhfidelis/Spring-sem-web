package com.henriquefidelis.screen_match.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(
        @JsonAlias("Title") String titulo,
        @JsonAlias("Year") String ano,
        @JsonAlias("totalSeasons") Integer totalDeTemporadas,
        @JsonAlias("imdbRating") String avaliacao,
        @JsonAlias("Genre")String genero,
        @JsonAlias("Actors") String atores,
        @JsonAlias("Poster")String poster,
        @JsonAlias("Plot")String sinopse
        ) {

}
