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
    public void createLearn(String email, String title, int section) {
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
        return resultData;
    }
}
