package com.felipepassada.outsera.infra.data.mapper;

import com.felipepassada.outsera.domain.model.Movie;
import com.felipepassada.outsera.domain.model.Producer;
import com.felipepassada.outsera.infra.data.parser.ProducerParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CsvMapper {

    private final ProducerParser producerParser;

    public Movie mapToEntity(String[] columns, Map<String, Integer> columnIndexMap, Map<String, Producer> producersCache) {
        int yearIndex = columnIndexMap.get("year");
        int titleIndex = columnIndexMap.get("title");
        int studiosIndex = columnIndexMap.get("studios");
        int producersIndex = columnIndexMap.get("producers");
        int winnerIndex = columnIndexMap.get("winner");

        Integer year = Integer.parseInt(columns[yearIndex].trim());
        String title = columns[titleIndex].trim();
        String studio = columns[studiosIndex].trim();
        String producersString = columns[producersIndex].trim();
        boolean winner = winnerIndex != -1
                && columns.length > winnerIndex
                && "yes".equalsIgnoreCase(columns[winnerIndex].trim());

        Movie movie = new Movie();
        movie.setYear(year);
        movie.setTitle(title);
        movie.setStudio(studio);
        movie.setWinner(winner);

        // Processa produtores usando a Strategy e o cache de persistência única
        Set<String> producerNames = producerParser.parse(producersString);
        for (String name : producerNames) {
            Producer producer = producersCache.computeIfAbsent(name, k -> {
                Producer p = new Producer();
                p.setName(k);
                p.setMovies(new ArrayList<>());
                return p;
            });

            // Associação bidirecional em nível de domínio
            movie.getProducers().add(producer);
            producer.getMovies().add(movie);
        }

        return movie;
    }
}
