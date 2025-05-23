package com.exchanger.repository;

import com.exchanger.entity.ConversionDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConversionDetailsRepository extends JpaRepository<ConversionDetails, Long> {
    Page<ConversionDetails> findByDate(LocalDateTime date, Pageable pageable);
}
