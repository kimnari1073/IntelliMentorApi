package org.intelli.intellimentor.service;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.repository.VocaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
@Log4j2
public class LearnServiceTests {
    @Autowired
    private VocaRepository vocaRepository;

    @Test
    public void testCreateLearn(){
        String email = "user1@aaa.com";
        String title="토익";
        int requestSection = 3;

        List<Voca> vocaList = vocaRepository.findByUserIdAndTitle(email,title);
        for (int i = 0; i < vocaList.size(); i++) {
            int section = (i % requestSection) + 1;
            vocaList.get(i).setSection(section);
        }
        vocaRepository.saveAll(vocaList);
    }

    //section:1
    //word:{key:value},{key:value}...
    //section:2
    //word:{key:value}...
    @Test
    public void testReadLearn(){
        String email="user1@aaa.com";
        String title="토익";
        List<Object[]>result = vocaRepository.findDataGroupBySection(email,title);
        Map<Integer, List<Map<String, Object>>> sectionMap = new LinkedHashMap<>();

        for (Object[] row : result) {
            Integer section = (Integer) row[0];
            String eng = (String) row[1];
            String kor = (String) row[2];
            Boolean bookmark = (Boolean) row[3];
            Integer mistakes = (Integer) row[4];

            // 단어의 세부 정보를 Map으로 저장
            Map<String, Object> wordMap = new LinkedHashMap<>();
            wordMap.put("eng", eng);
            wordMap.put("kor", kor);
            wordMap.put("bookmark", bookmark);
            wordMap.put("mistakes", mistakes);

            // 해당 섹션에 단어 추가
            sectionMap.computeIfAbsent(section, k -> new ArrayList<>()).add(wordMap);
        }

        // JSON 변환을 위한 구조 생성
        List<Map<String, Object>> sections = new ArrayList<>();
        for (Map.Entry<Integer, List<Map<String, Object>>> entry : sectionMap.entrySet()) {
            Map<String, Object> sectionData = new LinkedHashMap<>();
            sectionData.put("section", entry.getKey());
            sectionData.put("words", entry.getValue());
            sections.add(sectionData);
        }

        // 최종 JSON 데이터 구조
        Map<String, Object> resultData = new LinkedHashMap<>();
        resultData.put("data", sections);
        log.info(resultData);
    }

    @Test
    public void testUpdateLearn(){

    }

    @Test
    public void testDeleteLearn(){

    }
}
