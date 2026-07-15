package com.felipepassada.outsera.application.service;

import com.felipepassada.outsera.domain.dto.IntervalResultResponse;
import com.felipepassada.outsera.domain.model.Producer;
import com.felipepassada.outsera.domain.service.AwardIntervalCalculator;
import com.felipepassada.outsera.infra.repository.ProducerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProducerService {

    private final ProducerRepository producerRepository;
    private final AwardIntervalCalculator awardIntervalCalculator;

    public ProducerService(ProducerRepository producerRepository, AwardIntervalCalculator awardIntervalCalculator) {
        this.producerRepository = producerRepository;
        this.awardIntervalCalculator = awardIntervalCalculator;
    }

    public IntervalResultResponse getAwardIntervals() {
        log.info("Starting database query to fetch winning producers for interval calculation.");

        List<Producer> winnersWithMovies = producerRepository.findAllWinnersWithMovies();

        if (winnersWithMovies.isEmpty()) {
            log.warn("No winning producers were returned from the database.");
            return new IntervalResultResponse(List.of(), List.of());
        }

        log.debug("Starting award interval calculation for {} winning producers.", winnersWithMovies.size());
        IntervalResultResponse result = awardIntervalCalculator.calculateMinMaxAwardIntervals(winnersWithMovies);

        log.info("Award intervals successfully calculated. Min intervals count: {}, Max intervals count: {}.",
                result.min().size(), result.max().size());

        return result;
    }

}
