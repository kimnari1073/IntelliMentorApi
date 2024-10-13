package org.intelli.intellimentor.service;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.repository.AttendanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
