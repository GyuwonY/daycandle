package com.example.daycandle.repository;

import com.example.daycandle.model.DayCandle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayCandleRepository extends JpaRepository<DayCandle, Long> {
}
