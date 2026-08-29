package com.springclone.conversionservice.service;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class VideoIdExtractor {

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
        "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|.*[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})"
    );

    private static final Pattern SOUNDCLOUD_PATTERN = Pattern.compile(
        "(?:https?:\\/\\/)?(?:www\\.)?soundcloud\\.com\\/([a-zA-Z0-9_-]+)\\/([a-zA-Z0-9_-]+)"
    );

    public String extractVideoId(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        String trimmed = url.trim();

        Matcher ytMatcher = YOUTUBE_PATTERN.matcher(trimmed);
        if (ytMatcher.find()) {
            return ytMatcher.group(1);
        }

        Matcher scMatcher = SOUNDCLOUD_PATTERN.matcher(trimmed);
        if (scMatcher.find()) {
            return "sc-" + scMatcher.group(1) + "-" + scMatcher.group(2);
        }

        return null;
    }
}
