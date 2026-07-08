package com.felipepassada.outsera.infra.config;

import com.felipepassada.outsera.domain.model.Movie;
import com.felipepassada.outsera.domain.model.Producer;
import com.felipepassada.outsera.infra.repository.MovieRepository;
import com.felipepassada.outsera.infra.repository.ProducerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final ProducerRepository producerRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing data extraction and persistency...");

        if (movieRepository.count() > 0) {
            log.info("Database not empty. Skipping data initialization.");
            return;
        }

        try {
            ClassPathResource resource = new ClassPathResource("data/data.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            Map<String, Producer> producersMap = new HashMap<>();
            List<Movie> movies = new ArrayList<>();

            String line;
            Map<String, Integer> columnIndexMap = null;
            boolean isHeaderLine = true;

            while ((line = reader.readLine()) != null) {
                if (isHeaderLine) {
                    columnIndexMap = parseHeader(line);
                    isHeaderLine = false;
                    continue;
                }

                String[] columns = line.split(";");

                if (columns.length < 4) {
                    log.warn("Invalid line format: {}", line);
                    continue;
                }

                try {
                    Integer year = Integer.parseInt(columns[getColumnIndex(columnIndexMap, "year")].trim());
                    String title = columns[getColumnIndex(columnIndexMap, "title")].trim();
                    String studio = columns[getColumnIndex(columnIndexMap, "studios")].trim();
                    String producersString = columns[getColumnIndex(columnIndexMap, "producers")].trim();
                    Boolean winner = columns.length > getColumnIndex(columnIndexMap, "winner") && "yes".equalsIgnoreCase(columns[getColumnIndex(columnIndexMap, "winner")].trim());

                    Set<Producer> movieProducers = extractAndProcessProducers(producersString, producersMap);

                    Movie movie = new Movie();
                    movie.setYear(year);
                    movie.setTitle(title);
                    movie.setStudio(studio);
                    movie.setWinner(winner);
                    movie.setProducers(movieProducers);

                    movies.add(movie);

                } catch (NumberFormatException e) {
                    log.warn("Error while processing line: {}. Error Detail: {}", line, e.getMessage());
                }
            }

            reader.close();

            producerRepository.saveAll(producersMap.values());
            log.info("Producers persisted: {}", producersMap.size());

            movieRepository.saveAll(movies);
            log.info("Movies persisted: {}", movies.size());

            log.info("Successfully extracted data and persistence on DB!");

        } catch (Exception e) {
            log.error("Error while initializing data persistency: {}", e.getMessage(), e);
        }
    }

    private Map <String, Integer> parseHeader(String headerLine) {
        Map<String, Integer> headerMap = new HashMap<>();
        String[] headers = headerLine.split(";");

        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i].trim().toLowerCase(), i);
        }

        return headerMap;
    }

    private int getColumnIndex(Map<String, Integer> columnMap, String columnName) {
        Integer index = columnMap.get(columnName.toLowerCase());
        if (index == null) {
            throw new IllegalArgumentException("Column not found in header: " + columnName);
        }
        return index;
    }

    private Set<Producer> extractAndProcessProducers(String producersString, Map<String, Producer> producersMap) {
        Set<Producer> movieProducers = new HashSet<>();

        if (producersString == null || producersString.isEmpty()) {
            return movieProducers;
        }

        String normalizedString = producersString.replaceAll("\\s+and\\s+", ",");

        String[] producerNames = normalizedString.split(",");

        for (String producerName : producerNames) {
            String trimmedName = producerName.trim();

            if (!trimmedName.isEmpty()) {
                Producer producer = producersMap.computeIfAbsent(trimmedName, name -> {
                    Producer newProducer = new Producer();
                    newProducer.setName(name);
                    return newProducer;
                });

                movieProducers.add(producer);
            }
        }

        return movieProducers;
    }
}
