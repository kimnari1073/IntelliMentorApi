package org.intelli.intellimentor.repository;

import org.intelli.intellimentor.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserId(String userId);
    // 현재 요일에 해당하는 필드가 null인 출석 레코드를 조회하는 쿼리
    @Query("SELECT a FROM Attendance a WHERE " +
            "(CASE :dayOfWeek " +
            "WHEN 'MONDAY' THEN a.Mon " +
            "WHEN 'TUESDAY' THEN a.Tue " +
            "WHEN 'WEDNESDAY' THEN a.Wed " +
            "WHEN 'THURSDAY' THEN a.Thu " +
            "WHEN 'FRIDAY' THEN a.Fri " +
            "WHEN 'SATURDAY' THEN a.Sat " +
            "WHEN 'SUNDAY' THEN a.Sun " +
            "END) IS NULL")
    List<Attendance> findByDayOfWeekIsNull(@Param("dayOfWeek") String dayOfWeek);

}
