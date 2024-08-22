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
            @RequestBody LearnRequestDTO learnRequestDTO
    ){
        String email = JWTUtil.JWTtoEmail(authHeader);
        learnService.createLearn(email,learnRequestDTO.getTitle(),learnRequestDTO.getSection());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    //C토큰제목/ R U D
    //조회: 단어장제목,영어,한글,섹션 {[섹션:1,단어:[eng,kor]],[섹션,단어]}]
    //List<LearnDTO>
    //{
    // [section:1,
    //  word:[{apple:사과},{banana:바나나}]
    //  ],
    // [section:2,
    //  word:[{...},{...}]
    // ]
    //}

    //섹션 나누기
}
