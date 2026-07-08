package com.felipepassada.outsera.domain.dto;

public record ProducerAwardInterval(
        String producer,
        int interval,
        int previousWin,
        int followingWin
) {
}
