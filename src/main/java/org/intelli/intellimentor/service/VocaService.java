package org.intelli.intellimentor.service;

import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.intelli.intellimentor.dto.VocaModifyDTO;

import java.util.List;

public interface VocaService {
    void createVoca(VocaDTO vocaDTO);
    List<VocaListDTO> readVoca(VocaDTO vocaDTO);
    VocaDTO readDetailsVoca(VocaDTO vocaDTO);
    void updateVoca(VocaModifyDTO vocaModifyDTO);
    void deleteVoca(VocaDTO vocaDTO);
}
