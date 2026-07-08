package com.felipepassada.outsera.infra.repository;

import com.felipepassada.outsera.domain.model.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {

    @Query("SELECT DISTINCT p FROM Producer p INNER JOIN FETCH p.movies m WHERE m.winner = true")
    List<Producer> findAllWinnersWithMovies();
}
