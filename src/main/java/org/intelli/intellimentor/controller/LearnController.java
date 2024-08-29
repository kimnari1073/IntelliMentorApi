package org.intelli.intellimentor.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.LearnRequestDTO;
import org.intelli.intellimentor.service.LearnService;
import org.intelli.intellimentor.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/learn")
public class LearnController {
    private final LearnService learnService;

    //request: 토큰,제목,섹션
    @PostMapping("/create")
    public ResponseEntity<?> createLearn(
            @RequestHeader("Authorization")String authHeader,
            @RequestBody LearnRequestDTO learnRequestDTO){
        String email = JWTUtil.JWTtoEmail(authHeader);
        learnService.createLearn(email,learnRequestDTO.getTitle(),learnRequestDTO.getSection());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/read/{title}")
    public ResponseEntity<?> readLearn(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("title") String title){
        log.info("title: "+title);

        String email = JWTUtil.JWTtoEmail(authHeader);
        Map<String, Object> result = learnService.readLearn(email,title);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
