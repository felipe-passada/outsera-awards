package com.felipepassada.outsera.infra.repository;

import com.felipepassada.outsera.domain.model.Movie;
import com.felipepassada.outsera.domain.model.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

}
