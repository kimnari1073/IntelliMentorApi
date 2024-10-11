package org.intelli.intellimentor.repository;

import org.intelli.intellimentor.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
