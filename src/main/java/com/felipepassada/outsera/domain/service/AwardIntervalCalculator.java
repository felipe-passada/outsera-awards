package com.felipepassada.outsera.domain.service;

import com.felipepassada.outsera.domain.dto.IntervalResultResponse;
import com.felipepassada.outsera.domain.dto.ProducerAwardInterval;
import com.felipepassada.outsera.domain.model.Movie;
import com.felipepassada.outsera.domain.model.Producer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AwardIntervalCalculator {

    public IntervalResultResponse calculateMinMaxAwardIntervals(List<Producer> producers) {

        Map<Integer, List<ProducerAwardInterval>> intervalGroup = producers.stream()
                .flatMap(producer -> calculateIntervalsForProducer(producer).stream())
                .collect(java.util.stream.Collectors.groupingBy(ProducerAwardInterval::interval));

        if (intervalGroup.isEmpty()) {
            return new IntervalResultResponse(List.of(), List.of());
        }

        int minIntervalValue = intervalGroup.keySet().stream()
                .min(Integer::compare)
                .orElse(0);
        int maxIntervalValue = intervalGroup.keySet().stream()
                .max(Integer::compare)
                .orElse(0);

        List<ProducerAwardInterval> minIntervalProducers = intervalGroup.get(minIntervalValue);
        List<ProducerAwardInterval> maxIntervalProducers = intervalGroup.get(maxIntervalValue);

        return new IntervalResultResponse(minIntervalProducers, maxIntervalProducers);
    }

    private List<ProducerAwardInterval> calculateIntervalsForProducer(Producer producer) {
        var winningYears = producer.getMovies().stream()
                .filter(Movie::getWinner)
                .map(Movie::getYear)
                .sorted()
                .toList();

        List<ProducerAwardInterval> intervals = new ArrayList<>();

        for (int i = 1; i < winningYears.size(); i++) {
            int interval = winningYears.get(i) - winningYears.get(i - 1);
            intervals.add(new ProducerAwardInterval(producer.getName(), interval, winningYears.get(i - 1), winningYears.get(i)));
        }

        return intervals;
    }
}
