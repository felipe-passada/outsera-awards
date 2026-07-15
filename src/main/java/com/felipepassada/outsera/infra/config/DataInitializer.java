package com.felipepassada.outsera.infra.config;

import com.felipepassada.outsera.infra.data.service.MovieImporterService;
import com.felipepassada.outsera.infra.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final MovieImporterService movieImporterService;

    @Override
    public void run(String... args) {
        log.info("Initializing data extraction from CSV...");

        if(movieRepository.count() > 0) {
            log.info("Movies already exist in the database. Skipping data extraction.");
            return;
        }

        try {
            movieImporterService.importMoviesFromCsv("data/data.csv");
            log.info("Data extraction completed successfully.");
        } catch (Exception e) {
            log.error("Error during data extraction: {}", e.getMessage(), e);
        }
    }
}
