package com.springclone.conversionservice.dto;

public record ProcessResponse(
    String publicUrl,
    String title,
    String channel,
    long durationSeconds,
    String thumbnailUrl
) {}
