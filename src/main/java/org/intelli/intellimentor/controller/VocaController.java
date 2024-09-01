package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.intelli.intellimentor.service.VocaService;
import org.intelli.intellimentor.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> readVoca(@RequestHeader("Authorization") String authHeader){
        String email = JWTUtil.JWTtoEmail(authHeader);
        List<VocaListDTO> result = vocaService.readVoca(email);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //단어장 조회(수정)
    @GetMapping("/read/{title}")
    public ResponseEntity<VocaDTO> readDetailsVoca(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("title") String title){
        String email = JWTUtil.JWTtoEmail(authHeader);
        VocaDTO result = vocaService.readDetailsVoca(email,title);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //단어장 수정
    @PutMapping("/update/{title}")
    public ResponseEntity<String> updateVoca(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("title") String title,
            @RequestBody VocaDTO vocaDTO){
        String email = JWTUtil.JWTtoEmail(authHeader);
        vocaService.updateVoca(email,title,vocaDTO);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @DeleteMapping("/delete/{title}")
    public  ResponseEntity<String> deleteVoca(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("title") String title){
        String email = JWTUtil.JWTtoEmail(authHeader);
        vocaService.deleteVoca(email,title);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
