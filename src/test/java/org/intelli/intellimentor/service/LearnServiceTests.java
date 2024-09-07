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
        Long titleId = 1L;
        int requestSection = 3;
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

    //학습 조회
    @Test
    public void testGetLearn(){
        Long titleId=1L;

        List<Voca> vocaList = vocaRepository.getVocaListDetails(titleId);
        vocaList.sort(Comparator.comparing(voca -> voca.getSection().getSection()));
        List<Map<String, Object>> wordList = new ArrayList<>();
        List<Map<String, Object>> sectionsList = new ArrayList<>();

        int i=1;
        for (Voca row : vocaList) {
            if (row.getSection().getSection() != i) {
                // 이전 섹션의 데이터를 sectionsList에 추가
                Map<String, Object> sections = new LinkedHashMap<>();
                sections.put("section", i);
                sections.put("grade", row.getSection().getGrade());
                sections.put("word", wordList);
                sectionsList.add(sections);
                log.info("sectionsList: "+sectionsList);
                // 새로운 섹션 시작을 위해 wordList를 초기화
                wordList = new ArrayList<>();
                i++;
            }
            Map<String, Object> wordMap = new LinkedHashMap<>();
            wordMap.put("eng", row.getEng());
            wordMap.put("kor", row.getKor());
            wordMap.put("bookmark", row.isBookmark());
            wordMap.put("mistakes", row.getMistakes());
            wordList.add(wordMap);
//            log.info("wordList: "+wordList);
        }
        // 마지막 섹션 추가
        if (!wordList.isEmpty()) {
            Map<String, Object> sections = new LinkedHashMap<>();
            sections.put("section", i);
            sections.put("grade", vocaList.get(vocaList.size() - 1).getSection().getGrade());
            sections.put("word", wordList);
            sectionsList.add(sections);
        }
        Map<String, Object> resultData = new LinkedHashMap<>();
        resultData.put("titleId",vocaList.get(0).getTitle().getId());
        resultData.put("title",vocaList.get(0).getTitle().getTitle());
        resultData.put("maxSection",i);
        resultData.put("data", sectionsList);
        log.info(resultData);
    }

    //퀴즈 생성(Eng)
    @Test
    public void testGetQuizEng(){
        Long titleId = 1L;
        int section =1;
    }

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
