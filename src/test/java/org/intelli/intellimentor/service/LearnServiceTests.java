package org.intelli.intellimentor.service;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.repository.SectionRepository;
import org.intelli.intellimentor.repository.VocaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
@Log4j2
public class LearnServiceTests {

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private VocaRepository vocaRepository;
    //섹션 설정
    @Test
    public void testSetSection() {
        Long titleId = 2L;
        int requestSection = 4;
        //섹션 생성
        List<Section> saveSectionList = new ArrayList<>();
        for(int i=1;i<=requestSection;i++){
            Section section = Section.builder()
                    .section(i)
                    .build();
            saveSectionList.add(section);
        }
        sectionRepository.saveAll(saveSectionList);
        log.info("Section save.");

        //Voca섹션설정
        List<Voca> vocaList = vocaRepository.getVocaListDetails(titleId);
        int i =0;
        for(Voca row:vocaList){
            row.setSection(saveSectionList.get(i%requestSection));
            i++;
        }
        vocaRepository.saveAll(vocaList);
        log.info("Voca save..");
    }

    //섹션 초기화
    @Test
    public void testResetSection(){
        Long titleId = 2L;

        //Section 조회
        List<Long> sectionList = vocaRepository.getSectionList(titleId);

        //List<Voca> 조회 및 Section reset삭제
        List<Voca> vocaList = vocaRepository.getVocaListDetails(titleId);
        for(Voca row:vocaList){
            row.setSection(null);
        }
        vocaRepository.saveAll(vocaList);
        log.info("voca Save.");

        //Section 삭제
        sectionRepository.deleteAllById(sectionList);
        log.info("Section Delete..");


    }
//
//    //section:1
//    //word:{key:value},{key:value}...
//    //section:2
//    //word:{key:value}...
//    @Test
//    public void testReadLearn(){
//        String email="user1@aaa.com";
//        String title="토익";
//        List<Voca>result = vocaRepository.findVocaOrderBySection(email,title);
//        Map<Integer, List<Map<String, Object>>> sectionMap = new LinkedHashMap<>();
//
//        for (Voca row : result) {
//            Map<String, Object> wordMap = new LinkedHashMap<>();
//            wordMap.put("eng", row.getEng());
//            wordMap.put("kor", row.getKor());
//            wordMap.put("bookmark", row.isBookmark());
//            wordMap.put("mistakes", row.getMistakes());
//
//            // 해당 섹션에 단어 추가
//            sectionMap.computeIfAbsent(row.getSection(), k -> new ArrayList<>()).add(wordMap);
//        }
//
//        // JSON 변환을 위한 구조 생성
//        List<Map<String, Object>> sections = new ArrayList<>();
//        for (Map.Entry<Integer, List<Map<String, Object>>> entry : sectionMap.entrySet()) {
//            Map<String, Object> sectionData = new LinkedHashMap<>();
//            sectionData.put("section", entry.getKey());
//            sectionData.put("words", entry.getValue());
//            sections.add(sectionData);
//        }
//
//        // 최종 JSON 데이터 구조
//        Map<String, Object> resultData = new LinkedHashMap<>();
//        resultData.put("data", sections);
//        log.info(resultData);
//    }
//
//    @Test
//    public void testUpdateLearn(){
//
//    }
//
//    @Test
//    public void testDeleteLearn(){
//
//    }
//
//    @Test
//    public void testCreateQuiz(){
//
//    }
}
