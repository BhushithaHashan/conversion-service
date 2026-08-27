package com.springclone.conversionservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ConvertRequest(
    @NotBlank(message = "youtubeUrl is required")
    String youtubeUrl
) {}
