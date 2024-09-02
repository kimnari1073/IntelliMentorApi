//package org.intelli.intellimentor.repository;
//
//import lombok.extern.log4j.Log4j2;
//import org.intelli.intellimentor.domain.Voca;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.*;
//
//@SpringBootTest
//@Log4j2
//public class LearnRepositoryTests {
//    @Autowired
//    private VocaRepository vocaRepository;
//
//    @Test
//    public void testEngToKorQUiz(){
//        String email = "user1@aaa.com";
//        String title = "토익";
//        int section = 1;
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
//        log.info(Map.of("data",result));
//
//    }
//
//    @Test
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
//}
