package org.intelli.intellimentor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.intelli.intellimentor.dto.Voca.VocaAiDTO;
import org.intelli.intellimentor.dto.Voca.VocaDTO;
import org.intelli.intellimentor.dto.Voca.VocaItemDTO;
import org.intelli.intellimentor.dto.Voca.VocaUpdateDTO;

import java.util.Map;

public interface VocaService {
    VocaItemDTO getHomeVoca(String userId);
    void createVoca(String email,VocaDTO vocaDTO);
    void createVocaByAi(String email, VocaAiDTO vocaAiDTO) throws JsonProcessingException;
    Map<String,Object> getVocaList(String email);
    Map<String,Object> getVocaListDetails(Long titleId);
    void updateVoca(String email, Long titleId, VocaUpdateDTO vocaUpdateDTO);
    void deleteVoca(Long title);
}
