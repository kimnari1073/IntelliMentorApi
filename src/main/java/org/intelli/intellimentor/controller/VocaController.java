package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.service.VocaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/voca")
public class VocaController {
    private final VocaService vocaService;

    @PostMapping("/create")
    public ResponseEntity<Map<String,String>> createVoca(@RequestBody VocaDTO vocaDTO){
        vocaService.createVoca(vocaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("title",vocaDTO.getTitle()));

    }

}
