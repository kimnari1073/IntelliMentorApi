package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import org.intelli.intellimentor.domain.VocaList;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.intelli.intellimentor.repository.VocaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VocaServiceImpl implements VocaService{

    private final ModelMapper modelMapper;
    private final VocaRepository vocaRepository;

    @Override
    public Long register(VocaListDTO vocaListDTO) {
        VocaList vocaList = modelMapper.map(vocaListDTO, VocaList.class);
        VocaList savedVocaList = vocaRepository.save(vocaList);
        return savedVocaList.getVocaListId();

    }
}
