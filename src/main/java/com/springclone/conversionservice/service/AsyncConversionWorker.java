package com.springclone.conversionservice.service;

import com.springclone.conversionservice.dto.MetadataRequest;
import com.springclone.conversionservice.dto.ProcessRequest;
import com.springclone.conversionservice.dto.ProcessResponse;
import com.springclone.conversionservice.model.ConversionJob;
import com.springclone.conversionservice.model.JobStatus;
import com.springclone.conversionservice.repository.ConversionJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class AsyncConversionWorker {

    private static final Logger log = LoggerFactory.getLogger(AsyncConversionWorker.class);
    private static final String PROCESSING_SERVICE_URL = "http://PROCESSING-SERVICE/api/process"\;
    private static final String METADATA_SERVICE_URL = "http://METADATA-SERVICE/api/metadata"\;

    private final ConversionJobRepository repository;
    private final RestTemplate restTemplate;

    public AsyncConversionWorker(ConversionJobRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @Async("taskExecutor")
    public void runProcessingPipelineAsync(Long jobId) {
        Optional<ConversionJob> jobOpt = repository.findById(jobId);
        if (jobOpt.isEmpty()) return;

        ConversionJob job = jobOpt.get();
        job.setStatus(JobStatus.PROCESSING);
        repository.save(job);

        try {
            ProcessRequest processReq = new ProcessRequest(job.getVideoId(), job.getYoutubeUrl());
            ProcessResponse processRes = restTemplate.postForObject(PROCESSING_SERVICE_URL, processReq, ProcessResponse.class);

            if (processRes == null || processRes.publicUrl() == null) {
                throw new RuntimeException("Processing service returned an empty or invalid response.");
            }

            try {
                MetadataRequest metadataReq = new MetadataRequest(
                    job.getVideoId(),
                    job.getYoutubeUrl(),
                    processRes.title(),
                    processRes.channel(),
                    processRes.durationSeconds(),
                    processRes.thumbnailUrl()
                );
                restTemplate.postForObject(METADATA_SERVICE_URL, metadataReq, String.class);
            } catch (Exception metadataEx) {
                log.warn("Failed to persist metadata for videoId {}: {}", job.getVideoId(), metadataEx.getMessage());
            }

            job.setStatus(JobStatus.DONE);
            job.setDownloadUrl(processRes.publicUrl());
            repository.save(job);

        } catch (Exception e) {
            log.error("Conversion pipeline failed for job ID {}: {}", jobId, e.getMessage());
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            repository.save(job);
        }
    }
}
