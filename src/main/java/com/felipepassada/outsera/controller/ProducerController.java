package com.felipepassada.outsera.controller;

import com.felipepassada.outsera.application.service.ProducerService;
import com.felipepassada.outsera.domain.dto.IntervalResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producers")
public class ProducerController {

    @Autowired
    ProducerService producerService;

    @GetMapping
    public ResponseEntity<IntervalResultResponse> getProducerAwardIntervals() {
        IntervalResultResponse response = producerService.getAwardIntervals();
        return ResponseEntity.ok(response);
    }
}
