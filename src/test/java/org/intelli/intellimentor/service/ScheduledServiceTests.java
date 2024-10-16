package org.intelli.intellimentor.service;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Attendance;
import org.intelli.intellimentor.repository.AttendanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Log4j2
public class ScheduledServiceTests {
    @Autowired
    private ScheduledService scheduledService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Test
    public void getAttendance(){
        String userId = "user1@aaa.com";
        log.info(attendanceRepository.findByUserId(userId));
    }

    @Test
    public void notAttendance(){

        LocalDate today = LocalDate.now();
        String dayOfWeek = LocalDate.now().getDayOfWeek().name();

        // 오늘의 출석을 처리하지 않은 레코드만 가져오기
        List<Attendance> attendances = attendanceRepository.findByDayOfWeekIsNull(dayOfWeek);

        for (Attendance attendance : attendances) {
            switch (today.getDayOfWeek()) {
                case MONDAY -> attendance.setMon(false);
                case TUESDAY -> attendance.setTue(false);
                case WEDNESDAY -> attendance.setWed(false);
                case THURSDAY -> attendance.setThu(false);
                case FRIDAY -> attendance.setFri(false);
                case SATURDAY -> attendance.setSat(false);
                case SUNDAY -> attendance.setSun(false);
            }
        }
        attendanceRepository.saveAll(attendances);
    }

}
