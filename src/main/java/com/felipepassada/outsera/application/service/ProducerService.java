package com.felipepassada.outsera.application.service;

import com.felipepassada.outsera.domain.dto.ProducerAwardInterval;
import com.felipepassada.outsera.domain.dto.IntervalResultResponse;
import com.felipepassada.outsera.domain.model.Movie;
import com.felipepassada.outsera.domain.model.Producer;
import com.felipepassada.outsera.infra.repository.ProducerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProducerService {

    @Autowired
    private ProducerRepository producerRepository;

    public IntervalResultResponse getAwardIntervals() {

        var producers = producerRepository.findAllWinnersWithMovies();

        List<ProducerAwardInterval> allIntervals = producers.stream()
                .flatMap(producer -> calculateIntervalsForProducer(producer).stream())
                .toList();

        if (allIntervals.isEmpty()) {
            return new IntervalResultResponse(List.of(), List.of());
        }

        int minIntervalValue = allIntervals.stream()
                .mapToInt(ProducerAwardInterval::interval)
                .min()
                .orElse(Integer.MAX_VALUE);

        int maxIntervalValue = allIntervals.stream()
                .mapToInt(ProducerAwardInterval::interval)
                .max()
                .orElse(Integer.MIN_VALUE);

        List<ProducerAwardInterval> minIntervals = allIntervals.stream()
                .filter(prodAwIn -> prodAwIn.interval() == minIntervalValue)
                .toList();

        List<ProducerAwardInterval> maxIntervals = allIntervals.stream()
                .filter(prodAwIn -> prodAwIn.interval() == maxIntervalValue)
                .toList();

        return new IntervalResultResponse(minIntervals, maxIntervals);

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
