package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Voca;
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
    @Override
    public void setSection(Long titleId, int requestSection) {
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
    }
    @Override
    public void deleteLearn(Long titleId) {
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
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLearn(Long titleId) {
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
        return resultData;
    }
//
//    @Override
//    public Map<String, Object> getQuizEng(String email, String title, int section) {
//        List<Voca> listVoca = vocaRepository.findByUserIdAndTitleAndSection(email,title,section);
//        List<List<String>> result = new ArrayList<>();
//
//        for (Voca voca:
//                listVoca) {
//            List<String> tem = new ArrayList<>();
//            List<Voca> choices = testFindChoices(listVoca,voca);
//            for (Voca v : choices) {
//                tem.add(v.getKor());
//            }
//            tem.add(voca.getKor());
//            Collections.shuffle(tem);
//            tem.add(0,voca.getEng());
//            result.add(tem);
//        }
//        Collections.shuffle(result);
//        return Map.of("data",result);
//    }
//
//    @Override
//    public Map<String, Object> getQuizKor(String email, String title, int section) {
//        List<Voca> listVoca = vocaRepository.findByUserIdAndTitleAndSection(email,title,section);
//        List<List<String>> result = new ArrayList<>();
//
//        for (Voca voca:
//                listVoca) {
//            List<String> tem = new ArrayList<>();
//            List<Voca> choices = testFindChoices(listVoca,voca);
//            for (Voca v : choices) {
//                tem.add(v.getEng());
//            }
//            tem.add(voca.getEng());
//            Collections.shuffle(tem);
//            tem.add(0,voca.getKor());
//            result.add(tem);
//        }
//        Collections.shuffle(result);
//        return Map.of("data",result);
//    }
//
//
//    private List<Voca> testFindChoices(List<Voca> listVoca, Voca excludedVoca){
//        List<Voca> filteredList = new ArrayList<>(listVoca);// 원본 리스트 복사
//
//        filteredList.remove(excludedVoca);
//        Collections.shuffle(filteredList);
//
//        // 상위 3개 요소를 선택, 반환
//        List<Voca> randomVocaList = filteredList.subList(0, Math.min(3, filteredList.size()));
//        return randomVocaList;
//    }

}
