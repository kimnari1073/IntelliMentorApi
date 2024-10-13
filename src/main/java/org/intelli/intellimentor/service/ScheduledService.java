package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Attendance;
import org.intelli.intellimentor.domain.Member;
import org.intelli.intellimentor.repository.AttendanceRepository;
import org.intelli.intellimentor.repository.MemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ScheduledService {
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    public Attendance getAttendance(String userId){
        return attendanceRepository.findByUserId(userId).orElse(null);
    }


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
            attendance.setWed(null);
            attendance.setThu(null);
            attendance.setFri(null);
            attendance.setSat(null);
            attendance.setSun(null);
        }

        // 변경 사항을 데이터베이스에 저장
        attendanceRepository.saveAll(attendances);
    }

    @Transactional
    public void markAttendanceAsPresent(String email) {
        // 이메일로 사용자를 조회
        Member member = memberRepository.findById(email).orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자에 해당하는 출석 기록 조회 또는 생성
        Attendance attendance = attendanceRepository.findByUserId(member.getEmail())
                .orElse(Attendance.builder()
                        .userId(member.getEmail())  // userId 설정
                        .build());

        // 오늘 날짜에 해당하는 출석 필드를 true로 변경
        LocalDate today = LocalDate.now();
        switch (today.getDayOfWeek()) {
            case MONDAY:
                attendance.setMon(true);
                break;
            case TUESDAY:
                attendance.setTue(true);
                break;
            case WEDNESDAY:
                attendance.setWed(true);
                break;
            case THURSDAY:
                attendance.setThu(true);
                break;
            case FRIDAY:
                attendance.setFri(true);
                break;
            case SATURDAY:
                attendance.setSat(true);
                break;
            case SUNDAY:
                attendance.setSun(true);
                break;
        }

        // 출석 기록을 저장
        attendanceRepository.save(attendance);
    }
}
