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

    @PatchMapping("/set/{titleId}")
    public ResponseEntity<?> setSection(
            @PathVariable("titleId")Long titleId,
            @RequestBody Map<String, Integer> body
    ){
        learnService.setSection(titleId,body.get("section"));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

//    @GetMapping("/read/{title}")
//    public ResponseEntity<?> readLearn(
//            @RequestHeader("Authorization") String authHeader,
//            @PathVariable("title") String title){
//        String email = JWTUtil.JWTtoEmail(authHeader);
//        Map<String, Object> result = learnService.readLearn(email,title);
//        return ResponseEntity.status(HttpStatus.OK).body(result);
//    }
//
//    @GetMapping("/quiz/eng/{title}/{section}")
//    public ResponseEntity<?> getQuizEng(
//            @RequestHeader("Authorization") String authHeader,
//            @PathVariable("title") String title,
//            @PathVariable("section") int section){
//        String email = JWTUtil.JWTtoEmail(authHeader);
//        Map<String, Object> result = learnService.getQuizEng(email,title,section);
//        return ResponseEntity.status(HttpStatus.OK).body(result);
//    }
//    @GetMapping("quiz/kor/{title}/{section}")
//    public ResponseEntity<?> getQuizKor(
//            @RequestHeader("Authorization") String authHeader,
//            @PathVariable("title") String title,
//            @PathVariable("section") int section){
//        String email = JWTUtil.JWTtoEmail(authHeader);
//        Map<String, Object> result = learnService.getQuizKor(email,title,section);
//        return ResponseEntity.status(HttpStatus.OK).body(result);
//    }
//
//
//    //학습초기화
//    @DeleteMapping("/reset/{title}")
//    public ResponseEntity<?> deleteLearn(
//            @RequestHeader("Authorization") String authHeader,
//            @PathVariable("title") String title){
//        String email = JWTUtil.JWTtoEmail(authHeader);
//        learnService.deleteLearn(email,title);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
}
