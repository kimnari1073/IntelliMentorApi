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
import java.util.NoSuchElementException;
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
    public List<VocaListDTO> readVoca(VocaDTO vocaDTO) {
        List<Object[]> result = vocaRepository.getVocaCount(vocaDTO.getUserId());

        return result.stream()
                .map(r-> new VocaListDTO((String)r[0],(Long)r[1]))
                .toList();
    }

    @Override
    public VocaDTO readDetailsVoca(VocaDTO vocaDTO) {
        List<Voca> result = vocaRepository.findByUserIdAndTitle(vocaDTO.getUserId(),vocaDTO.getTitle());
        VocaDTO responseDTO = new VocaDTO();

        responseDTO.setUserId(result.get(1).getUserId());
        responseDTO.setTitle(result.get(1).getTitle());

        List<String> eng = new ArrayList<>();
        List<String> kor = new ArrayList<>();
        for (Voca voca : result) {
            eng.add(voca.getEng());
            kor.add(voca.getKor());
        }

        responseDTO.setEng(eng);
        responseDTO.setKor(kor);

        return responseDTO;
    }

    @Override
    public void deleteVoca(VocaDTO vocaDTO) {
        String userId=vocaDTO.getUserId();
        String title= vocaDTO.getTitle();
        List<Voca> result = vocaRepository.findByUserIdAndTitle(userId,title);
        if(result.isEmpty()){
            throw new NoSuchElementException();
        }
        vocaRepository.deleteByUserIdAndTitle(userId,title);
    }
}
