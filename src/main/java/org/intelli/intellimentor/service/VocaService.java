package org.intelli.intellimentor.service;

import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.dto.VocaUpdateDTO;

import java.util.Map;

public interface VocaService {
    void createVoca(String email,VocaDTO vocaDTO);
    Map<String,Object> getVocaList(String email);
    Map<String,Object> getVocaListDetails(Long titleId);
    void updateVoca(String email, VocaUpdateDTO vocaUpdateDTO);
    void deleteVoca(Long title);
}
