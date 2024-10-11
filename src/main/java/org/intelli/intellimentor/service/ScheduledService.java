package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Attendance;
import org.intelli.intellimentor.repository.AttendanceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ScheduledService {
    private final AttendanceRepository attendanceRepository;

    // 매주 월요일 00:00에 실행되는 스케줄러
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void resetAttendanceFields() {
        // 모든 출석 레코드 가져오기
        List<Attendance> attendances = attendanceRepository.findAll();

        // 각 출석 기록의 Mon ~ Sun 필드를 null로 설정
        for (Attendance attendance : attendances) {
            attendance.setMon(null);
            attendance.setTue(null);
            attendance.setWen(null);
            attendance.setThu(null);
            attendance.setFri(null);
            attendance.setSat(null);
            attendance.setSun(null);
        }

        // 변경 사항을 데이터베이스에 저장
        attendanceRepository.saveAll(attendances);
    }
}
