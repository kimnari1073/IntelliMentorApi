package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.intelli.intellimentor.repository.VocaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class VocaServiceImpl implements VocaService{

    private final VocaRepository vocaRepository;

    @Override
    public void createVoca(VocaDTO vocaDTO) {
        List<Voca> vocaList = new ArrayList<>();

        for (int i = 0; i < vocaDTO.getEng().size(); i++) {
            Voca voca = Voca.builder()
                    .userId(vocaDTO.getUserId())
                    .title(vocaDTO.getTitle())
                    .eng(vocaDTO.getEng().get(i))
                    .kor(vocaDTO.getKor().get(i))
                    .build();
            vocaList.add(voca);
        }

        vocaRepository.saveAll(vocaList);
    }

    @Override
    public List<VocaListDTO> readVoca(String userId) {
        List<Object[]> result = vocaRepository.getVocaCount(userId);

        return result.stream()
                .map(r-> new VocaListDTO((String)r[0],(Long)r[1]))
                .toList();
    }
}
