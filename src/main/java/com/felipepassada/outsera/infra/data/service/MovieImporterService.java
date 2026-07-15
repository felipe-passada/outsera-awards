package com.felipepassada.outsera.infra.data.service;

import com.felipepassada.outsera.domain.model.Movie;
import com.felipepassada.outsera.domain.model.Producer;
import com.felipepassada.outsera.infra.data.mapper.CsvMapper;
import com.felipepassada.outsera.infra.repository.MovieRepository;
import com.felipepassada.outsera.infra.repository.ProducerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieImporterService {

    private final MovieRepository movieRepository;
    private final ProducerRepository producerRepository;
    private final CsvMapper csvMapper;

    @Transactional
    public void importMoviesFromCsv(String csvFilePath) {
        ClassPathResource resource = new ClassPathResource(csvFilePath);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            Map<String, Producer> producersCache = new HashMap<>();
            List<Movie> movies = new ArrayList<>();
            Map<String, Integer> columnIndexMap = null;

            String line = reader.readLine();
            if (line != null) {
                columnIndexMap = parseHeader(line);
            }

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(";");
                if (columns.length < 4) continue;

                try {
                    Movie movie = csvMapper.mapToEntity(columns, columnIndexMap, producersCache);
                    movies.add(movie);
                } catch (Exception e) {
                    log.warn("Skipping invalid CSV line: {}. Error: {}", line, e.getMessage());
                }
            }

            producerRepository.saveAll(producersCache.values());
            log.info("Total de produtores persistidos: {}", producersCache.size());

            movieRepository.saveAll(movies);
            log.info("Successfully imported {} movies and linked producers dynamically!", movies.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Integer> parseHeader(String headerLine) {
        Map<String, Integer> headerMap = new HashMap<>();
        String[] headers = headerLine.split(";");
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i].trim().toLowerCase(), i);
        }
        return headerMap;
    }
}
