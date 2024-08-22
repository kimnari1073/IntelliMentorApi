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
        List<Object[]>result = vocaRepository.findWordsGroupedBySection(email,title);
        Map<Integer, List<List<String>>> sectionMap = new LinkedHashMap<>();

        // 데이터 그룹화 및 변환
        for (Object[] row : result) {
            Integer section = (Integer) row[0];
            String eng = (String) row[1];
            String kor = (String) row[2];

            List<String> wordPair = Arrays.asList(eng, kor);

            sectionMap.computeIfAbsent(section, k -> new ArrayList<>()).add(wordPair);
        }

        // JSON 변환을 위한 구조 생성
        List<Map<String, Object>> sections = new ArrayList<>();
        for (Map.Entry<Integer, List<List<String>>> entry : sectionMap.entrySet()) {
            Map<String, Object> sectionData = new LinkedHashMap<>();
            sectionData.put("section", entry.getKey());
            sectionData.put("words", entry.getValue());
            sections.add(sectionData);
        }

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
