package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.controller.advice.exception.DuplicateDataException;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.intelli.intellimentor.repository.VocaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class VocaServiceImpl implements VocaService{

    private final VocaRepository vocaRepository;

    @Override
    public void createVoca(String email,VocaDTO vocaDTO) {
        List<Voca> result = vocaRepository.findByUserIdAndTitle(email, vocaDTO.getTitle());
        if(!result.isEmpty()){
            throw new DuplicateDataException("단어장 제목이 중복됩니다.");
        }
        List<Voca> vocaList = new ArrayList<>();

        for (int i = 0; i < vocaDTO.getEng().size(); i++) {
            Voca voca = Voca.builder()
                    .userId(email)
                    .title(vocaDTO.getTitle())
                    .eng(vocaDTO.getEng().get(i))
                    .kor(vocaDTO.getKor().get(i))
                    .build();
            vocaList.add(voca);
        }

        vocaRepository.saveAll(vocaList);
    }

    @Override
    public List<VocaListDTO> readVoca(String email) {
        List<Object[]> result = vocaRepository.getVocaList(email);
        if(result.isEmpty()) return null;
        return result.stream()
                .map(r-> new VocaListDTO((String)r[0],(Long)r[1],(int)r[2]))
                .toList();
    }

    @Override
    public VocaDTO readDetailsVoca(String email, String title) {
        List<Voca> result = vocaRepository.findByUserIdAndTitle(email,title);
        VocaDTO responseDTO = new VocaDTO();

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
    public void updateVoca(String email, String title,VocaDTO vocaDTO) {
        deleteVoca(email,title);
        createVoca(email,vocaDTO);
    }

    @Override
    public void deleteVoca(String email,String title) {
        List<Voca> result = vocaRepository.findByUserIdAndTitle(email,title);
        if(result.isEmpty()){
            throw new NoSuchElementException();
        }
        vocaRepository.deleteByUserIdAndTitle(email,title);
    }
}
