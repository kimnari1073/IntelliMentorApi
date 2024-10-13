package org.intelli.intellimentor.repository;

import org.intelli.intellimentor.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserId(String userId);
}
