package com.felipepassada.outsera.infra.repository;

import com.felipepassada.outsera.domain.model.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {

    @Query("SELECT DISTINCT p FROM Producer p JOIN FETCH p.movies m WHERE p.id IN " +
            "(SELECT DISTINCT prod.id FROM Movie mov JOIN mov.producers prod WHERE mov.winner = true)")
    List<Producer> findAllWinnersWithMovies();
}
