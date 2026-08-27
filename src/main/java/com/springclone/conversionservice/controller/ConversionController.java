package com.springclone.conversionservice.controller;

import com.springclone.conversionservice.dto.ConvertRequest;
import com.springclone.conversionservice.model.ConversionJob;
import com.springclone.conversionservice.service.ConversionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/convert")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping
    public ResponseEntity<ConversionJob> convert(@Valid @RequestBody ConvertRequest request) {
        ConversionJob job = conversionService.initiateConversion(request);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversionJob> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(conversionService.getJobById(id));
    }

    @GetMapping
    public ResponseEntity<List<ConversionJob>> listJobs() {
        return ResponseEntity.ok(conversionService.getAllJobs());
    }
}
