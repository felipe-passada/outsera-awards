package com.felipepassada.outsera.application;

import com.felipepassada.outsera.application.service.ProducerService;
import com.felipepassada.outsera.domain.dto.ProducerAwardInterval;
import com.felipepassada.outsera.domain.model.Movie;
import com.felipepassada.outsera.domain.model.Producer;
import com.felipepassada.outsera.infra.repository.MovieRepository;
import com.felipepassada.outsera.infra.repository.ProducerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class ProducerSerivceIntegrationTest {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private MovieRepository movieRepository;


    @BeforeEach()
    void setup() {
        movieRepository.deleteAll();
        producerRepository.deleteAll();

        Producer boDerek = producerRepository.save(new Producer(null, "Bo Derek", null));
        Producer buzzFeitshans = producerRepository.save(new Producer(null, "Buzz Feitshans", null));

        movieRepository.save(new Movie(null, "Movie 1", 1980, "Studio A", true, Set.of(boDerek)));
        movieRepository.save(new Movie(null, "Movie 2", 1986, "Studio B", true, Set.of(boDerek)));
        movieRepository.save(new Movie(null, "Movie 3", 1990, "Studio B", true, Set.of(buzzFeitshans)));
        movieRepository.save(new Movie(null, "Movie 4", 1999, "Studio C", true, Set.of(buzzFeitshans)));
    }


    @Test
    void testFindWinnersWithValidData() {
        // Act
        var result = producerService.getAwardIntervals();

        // Assert
        assertNotNull(result, "Resultado não deve ser nulo");
        assertNotNull(result.min(), "Min não deve ser nulo");
        assertNotNull(result.max(), "Max não deve ser nulo");

        assertEquals(1, result.min().size(), "Deve ter 1 produtor com intervalo mínimo");
        assertEquals(1, result.max().size(), "Deve ter 1 produtor com intervalo máximo");

        assertEquals("Bo Derek", result.min().get(0).producer(), "Bo Derek deve ter intervalo mínimo");
        assertEquals(6, result.min().get(0).interval(), "Intervalo de Bo Derek deve ser 6");

        assertEquals("Buzz Feitshans", result.max().get(0).producer(), "Buzz Feitshans deve ter intervalo máximo");
        assertEquals(9, result.max().get(0).interval(), "Intervalo de Buzz Feitshans deve ser 9");
    }

    @Test
    void testWithMultipleProducersHavingSameMinInterval() {
        // Arrange
        Producer producer1 = producerRepository.save(new Producer(null, "Producer 1", null));
        Producer producer2 = producerRepository.save(new Producer(null, "Producer 2", null));

        movieRepository.save(new Movie(null, "Movie A", 2000, "Studio A", true, Set.of(producer1)));
        movieRepository.save(new Movie(null, "Movie B", 2002, "Studio B", true, Set.of(producer1)));

        movieRepository.save(new Movie(null, "Movie C", 2010, "Studio C", true, Set.of(producer2)));
        movieRepository.save(new Movie(null, "Movie D", 2012, "Studio D", true, Set.of(producer2)));

        // Act
        var result = producerService.getAwardIntervals();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.min().size(), "Deve ter 2 produtores com intervalo mínimo");
        assertEquals(2, result.min().get(0).interval(), "Intervalo minimo deve ser 2");

        var minProducers = result.min().stream().map(ProducerAwardInterval::producer).toList();
        assertEquals(true, minProducers.contains("Producer 1"), "Producer 1 deve estar na lista de min");
        assertEquals(true, minProducers.contains("Producer 2"), "Producer 2 deve estar na lista de min");
    }

    @Test
    void testWithMultipleProducersHavingSameMaxInterval() {
        // Arrange
        Producer producer1 = producerRepository.save(new Producer(null, "Producer 1", null));
        Producer producer2 = producerRepository.save(new Producer(null, "Producer 2", null));

        movieRepository.save(new Movie(null, "Movie A", 2000, "Studio A", true, Set.of(producer1)));
        movieRepository.save(new Movie(null, "Movie B", 2010, "Studio B", true, Set.of(producer1)));

        movieRepository.save(new Movie(null, "Movie C", 2005, "Studio C", true, Set.of(producer2)));
        movieRepository.save(new Movie(null, "Movie D", 2015, "Studio D", true, Set.of(producer2)));

        // Act
        var result = producerService.getAwardIntervals();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.max().size(), "Deve ter 2 produtores com intervalo máximo");
        assertEquals(10, result.max().get(0).interval(), "Intervalo maximo deve ser 10");

        var maxProducers = result.max().stream().map(ProducerAwardInterval::producer).toList();
        assertEquals(true, maxProducers.contains("Producer 1"), "Producer 1 deve estar na lista de max");
        assertEquals(true, maxProducers.contains("Producer 2"), "Producer 2 deve estar na lista de max");
    }


    @Test
    void testWithNoWinners() {
        // Arrange
        movieRepository.deleteAll();
        producerRepository.deleteAll();

        Producer producer1 = producerRepository.save(new Producer(null, "Producer 1", null));
        Producer producer2 = producerRepository.save(new Producer(null, "Producer 2", null));

        movieRepository.save(new Movie(null, "Movie A", 2000, "Studio A", false, Set.of(producer1)));
        movieRepository.save(new Movie(null, "Movie B", 2002, "Studio B", false, Set.of(producer1)));

        movieRepository.save(new Movie(null, "Movie C", 2010, "Studio C", false, Set.of(producer2)));
        movieRepository.save(new Movie(null, "Movie D", 2012, "Studio D", false, Set.of(producer2)));

        // Act
        var result = producerService.getAwardIntervals();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.min().size(), "Deve ter 0 produtores com intervalo mínimo");
        assertEquals(0, result.max().size(), "Deve ter 0 produtores com intervalo máximo");
    }

    @Test
    void testWithSingleWinner() {
        // Arrange
        movieRepository.deleteAll();
        producerRepository.deleteAll();

        Producer producer1 = producerRepository.save(new Producer(null, "Producer 1", null));

        movieRepository.save(new Movie(null, "Movie A", 2000, "Studio A", true, Set.of(producer1)));
        movieRepository.save(new Movie(null, "Movie B", 2002, "Studio B", false, Set.of(producer1)));

        // Act
        var result = producerService.getAwardIntervals();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.min().size(), "Deve ter 0 produtores com intervalo mínimo");
        assertEquals(0, result.max().size(), "Deve ter 0 produtores com intervalo máximo");
    }

    @Test
    void testEmptyDatabase() {
        // Act
        movieRepository.deleteAll();
        producerRepository.deleteAll();
        var result = producerService.getAwardIntervals();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.min().size(), "Deve ter 0 produtores com intervalo mínimo");
        assertEquals(0, result.max().size(), "Deve ter 0 produtores com intervalo máximo");
    }


}
