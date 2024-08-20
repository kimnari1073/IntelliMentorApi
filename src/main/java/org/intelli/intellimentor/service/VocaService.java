package org.intelli.intellimentor.service;

import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.intelli.intellimentor.dto.VocaModifyDTO;

import java.util.List;

public interface VocaService {
    void createVoca(VocaDTO vocaDTO);
    List<VocaListDTO> readVoca(String email);
    VocaDTO readDetailsVoca(String email, String title);
    void updateVoca(VocaModifyDTO vocaModifyDTO);
    void deleteVoca(VocaDTO vocaDTO);
}
