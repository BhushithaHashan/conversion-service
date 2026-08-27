package com.springclone.conversionservice.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conversion_jobs", indexes = {
    @Index(name = "idx_video_id", columnList = "videoId", unique = true)
})
public class ConversionJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String videoId;

    @Column(nullable = false)
    private String youtubeUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(length = 1024)
    private String downloadUrl;

    @Column(length = 2048)
    private String errorMessage;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public ConversionJob() {}

    public ConversionJob(String videoId, String youtubeUrl, JobStatus status) {
        this.videoId = videoId;
        this.youtubeUrl = youtubeUrl;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getVideoId() { return videoId; }
    public String getYoutubeUrl() { return youtubeUrl; }
    public JobStatus getStatus() { return status; }
    public String getDownloadUrl() { return downloadUrl; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setVideoId(String videoId) { this.videoId = videoId; }
    public void setYoutubeUrl(String youtubeUrl) { this.youtubeUrl = youtubeUrl; }
    public void setStatus(JobStatus status) { this.status = status; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
