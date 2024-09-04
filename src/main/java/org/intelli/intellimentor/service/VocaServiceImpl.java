package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Title;
import org.intelli.intellimentor.dto.VocaUpdateDTO;
import org.intelli.intellimentor.repository.SectionRepository;
import org.intelli.intellimentor.repository.TitleRepository;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaDTO;
import org.intelli.intellimentor.repository.VocaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class VocaServiceImpl implements VocaService{

    private final VocaRepository vocaRepository;
    private final TitleRepository titleRepository;
    private final SectionRepository sectionRepository;

    @Override
    public void createVoca(String email,VocaDTO vocaDTO) {//email,VocaDTO(title,kor,eng)
        Title title = Title.builder().title(vocaDTO.getTitle()).build();
        titleRepository.save(title);

        List<Voca> vocaList = new ArrayList<>();
        for (int i = 0; i < vocaDTO.getEng().size(); i++) {
            Voca voca = Voca.builder()
                    .userId(email)
                    .eng(vocaDTO.getEng().get(i))
                    .kor(vocaDTO.getKor().get(i))
                    .title(title)
                    .build();
            vocaList.add(voca);
        }
        vocaRepository.saveAll(vocaList);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String,Object> getVocaList(String email) {
        List<Object[]> vocaList = vocaRepository.getVocaList(email);

        List<Map<String,Object>> resultList = new ArrayList<>();
        for(Object[] row : vocaList){
            Map<String,Object> vocaListMap = new LinkedHashMap<>();
            vocaListMap.put("titleId",row[0]);
            vocaListMap.put("title",row[1]);
            vocaListMap.put("count",row[2]);
            vocaListMap.put("section",row[3]);

            resultList.add(vocaListMap);
        }
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("data",resultList);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVocaListDetails(Long titleId) {
        String title = titleRepository.getTitle(titleId);
        List<Voca> vocaList = vocaRepository.getVocaListDetails(titleId);
        List<Map<String,Object>> wordList = new ArrayList<>();
        for(Voca row : vocaList){
            Map<String,Object> vocaListMap = new LinkedHashMap<>();
            vocaListMap.put("id",row.getId());
            vocaListMap.put("eng",row.getEng());
            vocaListMap.put("kor",row.getKor());

            wordList.add(vocaListMap);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("titleId",titleId);
        result.put("title",title);
        result.put("word",wordList);

        return result;
    }

    @Override
    public void updateVoca(String email,Long titleId, VocaUpdateDTO vocaUpdateDTO) {
        //영속성 컨텍스트로 관리되는 Title 객체
        Title title = titleRepository.findById(titleId)
                .orElseThrow(() -> new RuntimeException("Title not found"));

        List<Voca> modifiedList = vocaUpdateDTO.getModifiedWord();
        List<Long> deleteList = vocaUpdateDTO.getDeleteId();
        List<Voca> addList = vocaUpdateDTO.getAddWord();

        //title 수정
        if(vocaUpdateDTO.getModifiedTitle() != null &&
                !vocaUpdateDTO.getModifiedTitle().equals(title.getTitle())){
            title.setTitle(vocaUpdateDTO.getModifiedTitle());
            titleRepository.save(title);
            log.info("Title Modified...");
        }

        //Section이 있다면 null로 설정
        List<Long> sectionList = vocaRepository.getSectionList(title.getId());
        log.info("sectionList: "+sectionList);
        log.info("modifiedList: "+modifiedList);
        log.info("deleteList: "+deleteList);
        log.info("addList: "+addList);
        if ((!modifiedList.isEmpty() || !deleteList.isEmpty() || !addList.isEmpty())
                &&!sectionList.contains(null)){

            //List<Voca> 조회 및 Section reset삭제
            List<Voca> vocaList = vocaRepository.getVocaListDetails(title.getId());
            for(Voca row:vocaList){
                row.setSection(null);
            }
            vocaRepository.saveAll(vocaList);
            log.info("voca Save.");

            //Section 삭제
            sectionRepository.deleteAllById(sectionList);
            log.info("Section Delete..");
        }

        //수정
        if (!modifiedList.isEmpty()) {
            for(Voca voca : modifiedList){
                voca.setUserId(email);
                voca.setTitle(title);
            }
            vocaRepository.saveAll(modifiedList);
            log.info("Voca Modified...");
        }

        //삭제
        if(!deleteList.isEmpty()){
            vocaRepository.deleteAllById(deleteList);
            log.info("Voca Delete...");

        }

        //추가
        if(!addList.isEmpty()){
            for(Voca voca: addList){
                voca.setUserId(email);
                voca.setTitle(title);
            }
            vocaRepository.saveAll(addList);
            log.info("Voca Add...");

        }
    }

    @Override
    public void deleteVoca(Long title) {
        titleRepository.deleteById(title);
    }
}
