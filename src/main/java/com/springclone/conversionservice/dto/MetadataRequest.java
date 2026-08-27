package com.springclone.conversionservice.dto;

public record MetadataRequest(
    String videoId,
    String youtubeUrl,
    String title,
    String channel,
    long durationSeconds,
    String thumbnailUrl
) {}
