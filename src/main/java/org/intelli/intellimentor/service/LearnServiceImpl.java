//package org.intelli.intellimentor.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.intelli.intellimentor.domain.Voca;
//import org.intelli.intellimentor.repository.VocaRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//@Log4j2
//public class LearnServiceImpl implements LearnService{
//    private final VocaRepository vocaRepository;
//    @Override
//    public void createSection(String email, String title, int section) {
//        List<Voca> vocaList = vocaRepository.findByUserIdAndTitle(email,title);
//        //섹션 설정
//        for (int i = 0; i < vocaList.size(); i++) {
//            int sec = (i % section) + 1;
//            vocaList.get(i).setSection(sec);
//        }
//        vocaRepository.saveAll(vocaList);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public Map<String, Object> readLearn(String email, String title) {
//        List<Voca> result = vocaRepository.findVocaOrderBySection(email,title);
//        List<Map<String, Object>> wordList = new ArrayList<>();
//        List<Map<String, Object>> sectionsList = new ArrayList<>();
//
//        int i=1;
//        for (Voca row : result) {
//            if (row.getSection() != i) {
//                // 이전 섹션의 데이터를 sectionsList에 추가
//                Map<String, Object> sections = new LinkedHashMap<>();
//                sections.put("section", i);
//                sections.put("grade", row.getGrade());
//                sections.put("word", new ArrayList<>(wordList));
//                sectionsList.add(sections);
//
//                // 새로운 섹션 시작을 위해 wordList를 초기화
//                wordList.clear();
//                i = row.getSection();
//            }
//            Map<String, Object> wordMap = new LinkedHashMap<>();
//            wordMap.put("eng", row.getEng());
//            wordMap.put("kor", row.getKor());
//            wordMap.put("bookmark", row.isBookmark());
//            wordMap.put("mistakes", row.getMistakes());
//            wordList.add(wordMap);
//        }
//        // 마지막 섹션 추가
//        if (!wordList.isEmpty()) {
//            Map<String, Object> sections = new LinkedHashMap<>();
//            sections.put("section", i);
//            sections.put("grade", result.get(result.size() - 1).getGrade());
//            sections.put("word", wordList);
//            sectionsList.add(sections);
//        }
//
//        Map<String, Object> resultData = new LinkedHashMap<>();
//        resultData.put("title",result.get(0).getTitle());
//        resultData.put("data", sectionsList);
//        return resultData;
//    }
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
//    @Override
//    public void deleteLearn(String email, String title) {
//        vocaRepository.deleteLearn(email,title);
//    }
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
//
//}
