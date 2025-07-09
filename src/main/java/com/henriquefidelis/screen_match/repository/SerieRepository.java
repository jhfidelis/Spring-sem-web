package com.henriquefidelis.screen_match.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.henriquefidelis.screen_match.models.Categoria;
import com.henriquefidelis.screen_match.models.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalDeTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, double avaliacao);

    @Query("SELECT s FROM Serie s WHERE s.totalDeTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
    List<Serie> buscarSeriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao);

    
}
