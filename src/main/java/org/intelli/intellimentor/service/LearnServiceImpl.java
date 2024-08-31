package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.repository.VocaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class LearnServiceImpl implements LearnService{
    private final VocaRepository vocaRepository;
    @Override
    public void createSection(String email, String title, int section) {
        List<Voca> vocaList = vocaRepository.findByUserIdAndTitle(email,title);
        //섹션 설정
        for (int i = 0; i < vocaList.size(); i++) {
            int sec = (i % section) + 1;
            vocaList.get(i).setSection(sec);
        }
        vocaRepository.saveAll(vocaList);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> readLearn(String email, String title) {
        List<Voca>result = vocaRepository.findVocaOrderBySection(email,title);
        Map<Integer, List<Map<String, Object>>> sectionMap = new LinkedHashMap<>();

        for (Voca row : result) {
            Map<String, Object> wordMap = new LinkedHashMap<>();
            wordMap.put("eng", row.getEng());
            wordMap.put("kor", row.getKor());
            wordMap.put("bookmark", row.isBookmark());
            wordMap.put("mistakes", row.getMistakes());

            // 해당 섹션에 단어 추가
            sectionMap.computeIfAbsent(row.getSection(), k -> new ArrayList<>()).add(wordMap);
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
        return resultData;
    }

    @Override
    public void deleteLearn(String email, String title) {
        vocaRepository.deleteLearn(email,title);
    }
}
