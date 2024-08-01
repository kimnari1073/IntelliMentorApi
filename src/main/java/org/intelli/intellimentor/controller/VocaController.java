package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import org.intelli.intellimentor.service.VocaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/voca")
public class VocaController {
    private final VocaService vocaService;

}
