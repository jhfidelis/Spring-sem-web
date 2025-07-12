package com.henriquefidelis.screen_match.dto;

import com.henriquefidelis.screen_match.models.Categoria;

public record SerieDTO(Long id,
        String titulo, 
        Integer totalDeTemporadas,
        Double avaliacao, 
        Categoria genero, 
        String atores,
        String poster, 
        String sinopse) {
}
