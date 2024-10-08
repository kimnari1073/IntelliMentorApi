package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Title;
import org.intelli.intellimentor.dto.Voca.VocaHomeDTO;
import org.intelli.intellimentor.dto.Voca.VocaItemDTO;
import org.intelli.intellimentor.dto.Voca.VocaUpdateDTO;
import org.intelli.intellimentor.repository.SectionRepository;
import org.intelli.intellimentor.repository.TitleRepository;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.Voca.VocaDTO;
import org.intelli.intellimentor.repository.VocaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class VocaServiceImpl implements VocaService{

    private final VocaRepository vocaRepository;
    private final TitleRepository titleRepository;
    private final SectionRepository sectionRepository;

    @Override
    public VocaItemDTO getHomeVoca(String userId) {
        List<Voca> vocaList = vocaRepository.findByUserIdAndSectionIdIsNotNullAndSentenceEngIsNotNull(userId);
        List<Voca> topVocaList = vocaList.stream()
                .filter(voca -> voca.getMistakes() > 0) // mistakes 필드가 1 이상인 경우만 필터링
                .toList(); // 리스트로 변환

        VocaHomeDTO vocaHomeDTO;
        if (!topVocaList.isEmpty()) {
            // ThreadLocalRandom을 사용하여 랜덤하게 1개의 단어 선택
            Voca voca = topVocaList.get(ThreadLocalRandom.current().nextInt(topVocaList.size()));
            vocaHomeDTO = VocaHomeDTO.from(voca, voca.getSection().getId());
        } else { //틀린 단어가 없으면
            Voca voca = vocaList.get(ThreadLocalRandom.current().nextInt(vocaList.size()));
            vocaHomeDTO = VocaHomeDTO.from(voca, voca.getSection().getId());

        }
        return vocaHomeDTO;
    }
    //단어생성
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

    //단어 리스트 조회
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

    //단어 수정 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVocaListDetails(Long titleId) {
        String title = titleRepository.getTitle(titleId);
        List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(titleId);
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

        if ((!modifiedList.isEmpty() || !deleteList.isEmpty() || !addList.isEmpty())
                &&!sectionList.contains(null)){

            //List<Voca> 조회 및 Section reset삭제
            List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(title.getId());
            for(Voca row:vocaList){
                row.setSection(null);
                row.setMistakes(0);
                row.setBookmark(false);
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
