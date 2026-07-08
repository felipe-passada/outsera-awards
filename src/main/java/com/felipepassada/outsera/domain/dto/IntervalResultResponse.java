package com.felipepassada.outsera.domain.dto;

import java.util.List;

public record IntervalResultResponse(
        List <ProducerAwardInterval> min,
        List <ProducerAwardInterval> max
) {
}
