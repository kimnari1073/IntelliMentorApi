package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.Voca.VocaDTO;
import org.intelli.intellimentor.dto.Voca.VocaUpdateDTO;
import org.intelli.intellimentor.service.VocaService;
import org.intelli.intellimentor.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/voca")
public class VocaController {
    private final VocaService vocaService;

    //단어장 생성
    @PostMapping("/create")
    public ResponseEntity<?> createVoca(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody VocaDTO vocaDTO){
        String email = JWTUtil.JWTtoEmail(authHeader);

        vocaService.createVoca(email,vocaDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //단어장 조회(리스트)
    @GetMapping("/read")
    public ResponseEntity<?> getVocaList(
            @RequestHeader("Authorization") String authHeader){
        String email = JWTUtil.JWTtoEmail(authHeader);

        Map<String,Object> result = vocaService.getVocaList(email);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //단어장 조회(수정 클릭 시)
    @GetMapping("/read/{titleId}")
    public ResponseEntity<?> getVocaListDetails(
            @PathVariable("titleId") Long titleId){
        Map<String,Object> result = vocaService.getVocaListDetails(titleId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //단어장 수정
    @PatchMapping("/update/{titleId}")
    public ResponseEntity<String> updateVoca(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("titleId")Long titleId,
            @RequestBody VocaUpdateDTO vocaUpdateDTO){
        String email = JWTUtil.JWTtoEmail(authHeader);

        vocaService.updateVoca(email,titleId,vocaUpdateDTO);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
//    단어장 삭제
    @DeleteMapping("/delete/{titleId}")
    public  ResponseEntity<String> deleteVoca(
            @PathVariable("titleId") Long titleId){

        vocaService.deleteVoca(titleId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
