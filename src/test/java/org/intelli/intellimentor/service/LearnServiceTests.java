//package org.intelli.intellimentor.service;
//
//import lombok.extern.log4j.Log4j2;
//import org.intelli.intellimentor.domain.Voca;
//import org.intelli.intellimentor.repository.VocaRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.*;
//
//@SpringBootTest
//@Log4j2
//public class LearnServiceTests {
//    @Autowired
//    private VocaRepository vocaRepository;
//    @Autowired
//    private LearnService learnService;
//
//    @Test
//    public void testCreateLearn(){
//        String email = "user1@aaa.com";
//        String title="토익";
//        int requestSection = 3;
//
//        List<Voca> vocaList = vocaRepository.findByUserIdAndTitle(email,title);
//        for (int i = 0; i < vocaList.size(); i++) {
//            int section = (i % requestSection) + 1;
//            vocaList.get(i).setSection(section);
//        }
//        vocaRepository.saveAll(vocaList);
//    }
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
//}
