package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.intelli.intellimentor.service.VocaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/voca")
public class VocaController {
    private final VocaService vocaService;

    @PostMapping("/")
    public Map<String,Long> register(@RequestBody VocaListDTO vocaListDTO){
        Long vno = vocaService.register(vocaListDTO);
        return Map.of("VocaNO",vno);
    }
}
