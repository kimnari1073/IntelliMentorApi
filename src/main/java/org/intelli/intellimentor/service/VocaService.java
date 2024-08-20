package org.intelli.intellimentor.service;

import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.dto.VocaListDTO;

import java.util.List;

public interface VocaService {
    void createVoca(String email,VocaDTO vocaDTO);
    List<VocaListDTO> readVoca(String email);
    VocaDTO readDetailsVoca(String email, String title);
    void updateVoca(String email,String title,VocaDTO vocaDTO);
    void deleteVoca(String email,String title);
}
