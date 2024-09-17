package org.intelli.intellimentor.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.QuizRequestDTO;
import org.intelli.intellimentor.service.LearnService;
import org.intelli.intellimentor.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    //학습초기화
    @DeleteMapping("/reset/{titleId}")
    public ResponseEntity<?> deleteLearn(@PathVariable("titleId") Long titleId){
        learnService.deleteLearn(titleId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/read/{titleId}")
    public ResponseEntity<?> getLearn(@PathVariable("titleId") Long titleId){
        Map<String, Object> result = learnService.getLearn(titleId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/modify/bookmark/{titleId}")
    public ResponseEntity<?> modifyBookmark(
            @PathVariable("titleId") Long titleId,
            @RequestBody Map<String, List<Long>> body){
        log.info(body.get("trueIdList"));
        log.info(body.get("falseIdList"));
        log.info("Request Body: " + body);

        learnService.modifiyBookmark(titleId,body.get("trueIdList"),body.get("falseIdList"));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @GetMapping("/quiz/{eks}/{sectionId}")
    public ResponseEntity<?> getQuiz(
            @PathVariable("eks")String subject,
            @PathVariable("sectionId") Long sectionId){
        Map<String, Object> result = learnService.getQuiz(sectionId,subject);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/quiz/mark/{sectionId}")
    public ResponseEntity<?> markQuiz(
            @PathVariable("sectionId") Long sectionId,
            @RequestBody QuizRequestDTO quizRequestDTO){
        Map<String, Object> result = learnService.markQuiz(sectionId, quizRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
