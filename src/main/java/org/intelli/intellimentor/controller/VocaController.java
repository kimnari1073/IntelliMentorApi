package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.intelli.intellimentor.dto.VocaModifyDTO;
import org.intelli.intellimentor.service.VocaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    public ResponseEntity<Map<String,String>> createVoca(@RequestBody VocaDTO vocaDTO){
        vocaService.createVoca(vocaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("title",vocaDTO.getTitle()));

    }

    //단어장 조회(리스트)
    @GetMapping("/read")
    public ResponseEntity<List<VocaListDTO>> readVoca(@RequestBody VocaDTO vocaDTO){
        List<VocaListDTO> result = vocaService.readVoca(vocaDTO);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //단어장 조회(상세)
    @GetMapping("/read/details")
    public ResponseEntity<VocaDTO> readDetailsVoca(@RequestBody VocaDTO vocaDTO){
        VocaDTO result = vocaService.readDetailsVoca(vocaDTO);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //단어장 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateVoca(@RequestBody VocaModifyDTO vocaModifyDTO){
        vocaService.updateVoca(vocaModifyDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @DeleteMapping("/delete")
    public  ResponseEntity<String> deleteVoca(@RequestBody VocaDTO vocaDTO){
        vocaService.deleteVoca(vocaDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
