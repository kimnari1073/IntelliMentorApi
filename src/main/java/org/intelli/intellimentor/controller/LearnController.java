package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.QuizRequestDTO;
import org.intelli.intellimentor.dto.Voca.VocaAllDTO;
import org.intelli.intellimentor.dto.Voca.VocaSectionDTO;
import org.intelli.intellimentor.service.LearnService;
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
    //학습초기화
    @DeleteMapping("/reset/{titleId}")
    public ResponseEntity<?> deleteLearn(@PathVariable("titleId") Long titleId){
        learnService.deleteLearn(titleId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //학습 조회(전체)
    @GetMapping("/read/{titleId}")
    public ResponseEntity<?> getLearn(@PathVariable("titleId") Long titleId){
        VocaAllDTO result = learnService.getLearn(titleId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //학습 조회(섹션)
    @GetMapping("/read/section/{sectionId}")
    public ResponseEntity<?> getLearnBySection(@PathVariable("sectionId") Long sectionId){
        VocaSectionDTO result = learnService.getLearnBySection(sectionId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //북마크
    @PatchMapping("/bookmark/{vocaId}")
    public ResponseEntity<?> setBookmark(
            @PathVariable("vocaId") Long vocaId){

        learnService.setBookmark(vocaId);

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
