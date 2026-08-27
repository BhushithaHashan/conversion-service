package com.springclone.conversionservice.service;

import com.springclone.conversionservice.dto.ConvertRequest;
import com.springclone.conversionservice.exception.InvalidUrlException;
import com.springclone.conversionservice.exception.ResourceNotFoundException;
import com.springclone.conversionservice.model.ConversionJob;
import com.springclone.conversionservice.model.JobStatus;
import com.springclone.conversionservice.repository.ConversionJobRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ConversionService {

    private final ConversionJobRepository repository;
    private final VideoIdExtractor idExtractor;
    private final AsyncConversionWorker asyncWorker;

    public ConversionService(ConversionJobRepository repository,
                             VideoIdExtractor idExtractor,
                             AsyncConversionWorker asyncWorker) {
        this.repository = repository;
        this.idExtractor = idExtractor;
        this.asyncWorker = asyncWorker;
    }

    public ConversionJob initiateConversion(ConvertRequest request) {
        String videoId = idExtractor.extractVideoId(request.youtubeUrl());
        if (videoId == null) {
            throw new InvalidUrlException("Invalid YouTube URL: unable to parse video ID.");
        }

        Optional<ConversionJob> existingOpt = repository.findByVideoId(videoId);
        boolean shouldTriggerPipeline = false;
        ConversionJob job;

        if (existingOpt.isPresent()) {
            job = existingOpt.get();
            if (job.getStatus() == JobStatus.DONE) {
                long hoursOld = Duration.between(job.getUpdatedAt(), Instant.now()).toHours();
                if (hoursOld < 20) {
                    return job;
                } else {
                    job.setStatus(JobStatus.PENDING);
                    job.setDownloadUrl(null);
                    job.setErrorMessage(null);
                    job = repository.save(job);
                    shouldTriggerPipeline = true;
                }
            } else if (job.getStatus() == JobStatus.PROCESSING) {
                return job;
            } else if (job.getStatus() == JobStatus.FAILED) {
                job.setStatus(JobStatus.PENDING);
                job.setErrorMessage(null);
                job = repository.save(job);
                shouldTriggerPipeline = true;
            } else {
                shouldTriggerPipeline = true;
            }
        } else {
            job = new ConversionJob(videoId, request.youtubeUrl(), JobStatus.PENDING);
            job = repository.save(job);
            shouldTriggerPipeline = true;
        }

        if (shouldTriggerPipeline) {
            asyncWorker.runProcessingPipelineAsync(job.getId());
        }

        return job;
    }

    public ConversionJob getJobById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + id));
    }

    public List<ConversionJob> getAllJobs() {
        return repository.findAll();
    }
}
