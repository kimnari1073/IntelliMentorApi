package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.QuizItemDTO;
import org.intelli.intellimentor.dto.QuizRequestDTO;
import org.intelli.intellimentor.repository.SectionRepository;
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
    private final SectionRepository sectionRepository;

    //섹션 설정
    @Override
    public void setSection(Long titleId, int requestSection) {
        //섹션 생성 (vocaCount = null)
        List<Section> saveSectionList = new ArrayList<>();
        for (int i = 1; i <= requestSection; i++) {
            Section section = Section.builder()
                    .section(i)
                    .build();
            saveSectionList.add(section);
        }

        // Voca 섹션 설정
        int[] sectionVocaCount = new int[requestSection];
        List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(titleId);
        int i = 0;
        for (Voca row : vocaList) {
            // 섹션 할당
            Section section = saveSectionList.get(i % requestSection);
            row.setSection(section);

            // 해당 섹션의 카운트를 증가
            sectionVocaCount[i % requestSection]++;

            i++;
        }
        // 섹션별 카운트 저장
        for (int j = 0; j < requestSection; j++) {
            saveSectionList.get(j).setVocaCount(sectionVocaCount[j]);  // 각 섹션에 카운트 저장
        }
        sectionRepository.saveAll(saveSectionList);
        vocaRepository.saveAll(vocaList);
    }
    //섹션 삭제
    @Override
    public void deleteLearn(Long titleId) {
        //Section 조회
        List<Long> sectionList = vocaRepository.getSectionList(titleId);

        //List<Voca> 조회 및 Section reset삭제
        List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(titleId);
        for(Voca row:vocaList){
            row.setSection(null);
        }
        vocaRepository.saveAll(vocaList);
        log.info("voca Save.");

        //Section 삭제
        sectionRepository.deleteAllById(sectionList);
        log.info("Section Delete..");
    }

    //북마크 수정
    @Override
    public void modifiyBookmark(Long titleId, List<Long> trueIdList, List<Long> falseIdList) {
        List<Voca> vocaList1 = vocaRepository.getVocaByTitleAndIdIn(titleId,trueIdList);
        List<Voca> vocaList2 = vocaRepository.getVocaByTitleAndIdIn(titleId,falseIdList);
        if(!vocaList1.isEmpty()){
            for(Voca row:vocaList1){
                row.setBookmark(true);
            }
            vocaRepository.saveAll(vocaList1);
        }
        if(!vocaList2.isEmpty()){
            for(Voca row:vocaList2){
                row.setBookmark(false);
            }
            vocaRepository.saveAll(vocaList2);
        }


    }

    //학습 조회
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLearn(Long titleId) {
        Voca voca = vocaRepository.findFirstByTitleId(titleId);
        List<Long> sectionIdList = vocaRepository.getSectionList(titleId);
        Map<String,Object> resultMap = new LinkedHashMap<>();

        List<Map<String,Object>> dataList = new LinkedList<>();

        for(Long sectionId:sectionIdList){
            dataList.add(testGetSectionData(sectionId));
        }

        resultMap.put("title",voca.getTitle());
        resultMap.put("sectionMax",sectionIdList.size());
        resultMap.put("data",dataList);
        log.info(resultMap);
        return resultMap;
    }

    @Override
    public Map<String, Object> getQuiz(Long sectionId,String subject) {
        List<Voca> vocaList = vocaRepository.getVocaBySectionId(sectionId);
        List<List<Map<String, Object>>> result = new ArrayList<>(); // quizList가 아닌 List로 담음

        if (subject.contains("e")) {
            for (Voca row : vocaList) {
                List<Map<String, Object>> temList = testFindChoices(vocaList, row, "e");
                result.add(temList);
            }
        }
        if (subject.contains("k")) {
            for (Voca row : vocaList) {
                List<Map<String, Object>> temList = testFindChoices(vocaList, row, "k");
                result.add(temList);
            }
        }
        return Map.of("quiz",result);
    }

   //퀴즈 채점
    @Override
    public Map<String, Object> markQuiz(Long sectionId, QuizRequestDTO quizRequestDTO) {
        Map<String, Integer> scoreMap = new LinkedHashMap<>();
        scoreMap.put("e",null);
        scoreMap.put("k",null);
        scoreMap.put("s",null);
        List<Long> incorrectList = new LinkedList<>();

        for (QuizItemDTO row : quizRequestDTO.getData()) {
            String type = row.getType();

            if (row.getCorrect()) {
                scoreMap.put(type, scoreMap.get(type) == null ? 1 : scoreMap.get(type) + 1);
            } else {
                incorrectList.add(row.getId());  // 오답 리스트
                scoreMap.putIfAbsent(type, 0);  // null 일 경우 0으로 초기화
            }
        }

        //점수 업데이트 (백분율)
        Section section = sectionRepository.findById(sectionId).orElseThrow();
        Iterator<Map.Entry<String, Integer>> iterator = scoreMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();

            if (entry.getValue() == null) {
                iterator.remove();  // null 값인 경우 해당 키 삭제
            } else {
                int score = (int) ((double) entry.getValue() / section.getVocaCount() * 100);
                scoreMap.put(entry.getKey(), score);  // 점수 업데이트
            }
        }
        section.setEngScore(scoreMap.getOrDefault("e", section.getEngScore()));
        section.setKorScore(scoreMap.getOrDefault("k", section.getKorScore()));
        section.setSenScore(scoreMap.getOrDefault("s", section.getSenScore()));

        //grade계산
        String grade = null;
        if(section.getEngScore()==null||section.getKorScore()==null){
            grade = "-";
        }else{
            int score = (section.getEngScore()+section.getKorScore())/2;
            // 기본 등급 설정
            if (score >= 90) {
                grade = "A";
                if(section.getSenScore()>=90) grade+="+";
            } else if (score >= 80) {
                grade = "B";
                if(section.getSenScore()>=80) grade+="+";
            } else if (score >= 70) {
                grade = "C";
                if(section.getSenScore()>=70) grade+="+";
            } else if (score >= 60) {
                grade = "D";
                if(section.getSenScore()>=60) grade+="+";
            } else {
                grade = "F";
            }
        }
        section.setGrade(grade);

        //진행률
        int progress = (
                Optional.ofNullable(section.getEngScore()).orElse(0) +
                        Optional.ofNullable(section.getKorScore()).orElse(0) +
                        Optional.ofNullable(section.getSenScore()).orElse(0)
        ) / 3;
        section.setProgress(progress);
        sectionRepository.save(section);

        //결과 출력용
        List<Map<String,Object>> misList = new ArrayList<>();

        //mistakes 필드 수정 로직
        List<Voca> mistakesList = vocaRepository.findAllById(incorrectList);
        //빈도수 체크
        Map<Long, Integer> frequencyMap = new HashMap<>();
        for (Long id : incorrectList) {
            frequencyMap.put(id, frequencyMap.getOrDefault(id, 0) + 1);
        }

        //업데이트
        for (Voca row : mistakesList) {
            int frequency = frequencyMap.getOrDefault(row.getId(), 0);
            row.setMistakes(row.getMistakes() + frequency);
            // 반환용 데이터
            Map<String, Object> misMap = new LinkedHashMap<>();
            misMap.put("id", row.getId());
            misMap.put("eng", row.getEng());
            misMap.put("kor", row.getKor());
            misList.add(misMap);
        }
        vocaRepository.saveAll(mistakesList);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scoreMap",scoreMap);
        result.put("scoreEng",section.getEngScore());
        result.put("scoreKor",section.getKorScore());
        result.put("scoreSen",section.getSenScore());
        result.put("grade",section.getGrade());
        result.put("mistakes",misList);

        return result;
    }

    private List<Map<String,Object>> testFindChoices(List<Voca> listVoca, Voca mainVoca,String type){
        List<Voca> filteredList = new ArrayList<>(listVoca); // 원본 리스트 복사
        filteredList.remove(mainVoca); //서브 필드를 위한 메인 필드 제거
        Collections.shuffle(filteredList);

        List<Map<String, Object>> resultList = new ArrayList<>();

        // 메인 및 서브 필드 설정
        String mainField = type.equals("e") ? "eng" : "kor";
        String subField = type.equals("e") ? "kor" : "eng";

        // 메인 단어 추가
        resultList.add(Map.of("id", mainVoca.getId(), mainField, type.equals("e") ? mainVoca.getEng() : mainVoca.getKor()));

        // 랜덤으로 선택된 3개의 요소를 추가
        filteredList.stream()
                .limit(3) // 3개의 요소로 제한
                .forEach(voca -> resultList.add(
                        Map.of("id", voca.getId(), subField, type.equals("e") ? voca.getKor() : voca.getEng())));

        int randomInt = (int) (Math.random() * 4) + 1;
        resultList.add(randomInt,Map.of("id",mainVoca.getId(),subField, type.equals("e") ? mainVoca.getKor() : mainVoca.getEng()));

        return resultList;
    }
    private Map<String,Object> testGetSectionData(Long sectionId){
        List<Voca> vocaList = vocaRepository.findBySectionIdOrderById(sectionId);

        Map<String,Object> resultMap = new LinkedHashMap<>();

        List<Map<String,Object>> wordList = new LinkedList<>();
        for(Voca row : vocaList){
            Map<String, Object> wordMap = new LinkedHashMap<>();
            wordMap.put("id",row.getId());
            wordMap.put("eng", row.getEng());
            wordMap.put("kor", row.getKor());
            wordMap.put("bookmark", row.isBookmark());
            wordMap.put("mistakes", row.getMistakes());
            wordMap.put("sentence",row.getSentence());
            wordList.add(wordMap);
        }

        resultMap.put("section",vocaList.get(0).getSection());
        resultMap.put("wordList",wordList);

        return resultMap;
    }

}
