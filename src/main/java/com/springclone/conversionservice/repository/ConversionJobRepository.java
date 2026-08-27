package com.springclone.conversionservice.repository;

import com.springclone.conversionservice.model.ConversionJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConversionJobRepository extends JpaRepository<ConversionJob, Long> {
    Optional<ConversionJob> findByVideoId(String videoId);
}
