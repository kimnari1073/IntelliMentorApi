package org.intelli.intellimentor.repository;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SpringBootTest
@Log4j2
public class VocaRepositoryTests {
    @Autowired
    private VocaRepository vocaRepository;

    @Test
    public void testInsertVoca(){
        vocaRepository.deleteAll();
        List<Voca> vocaList1 = new ArrayList<>();
        List<Voca> vocaList2 = new ArrayList<>();
        int vocaList1Sec=2;
        int vocaList2Sec=4;
        String[] englishWords1 = {
                "perseverance", "elaborate", "contemplate", "subtle", "innovative",
                "benevolent", "resilient", "meticulous", "ephemeral", "lucid"};
        String[] meanings1 = {
                "인내", "정교한", "심사숙고하다", "미묘한", "혁신적인",
                "자비로운", "회복력이 강한", "꼼꼼한", "덧없는", "명쾌한"};
        String[] englishWords2 = {
                "itinerary", "backpacking", "destination", "passport", "adventure",
                "sightseeing", "tourist", "expedition", "journey", "explore",
                "accommodation", "landmark", "souvenir", "reservation", "excursion",
                "voyage", "customs", "itinerary", "navigation", "roadtrip",
                "camping", "resort", "travel", "trekking", "guidebook",
                "currency", "luggage", "embark", "transit", "visa"};
        String[] meanings2 = {
                "여행 일정", "배낭여행", "목적지", "여권", "모험",
                "관광", "관광객", "탐험", "여정", "탐험하다",
                "숙박", "랜드마크", "기념품", "예약", "소풍",
                "항해", "세관", "여행 일정", "항해", "로드트립",
                "캠핑", "리조트", "여행", "트레킹", "여행 안내서",
                "통화", "짐", "승선하다", "환승", "비자"};

        Random random = new Random();
        for(int i=0; i<englishWords1.length; i++){
            Voca voca = Voca.builder()
                    .eng(englishWords1[i])
                    .kor(meanings1[i])
                    .title("토익")
                    .userId("user1@aaa.com")
                    .bookmark(random.nextBoolean())
                    .mistakes(random.nextInt(10)+1)
                    .section(i%vocaList1Sec+1)
                    .build();
            vocaList1.add(voca);
        }
        for(int i=0;i< englishWords2.length;i++){
            Voca voca = Voca.builder()
                    .eng(englishWords2[i])
                    .kor(meanings2[i])
                    .title("여행관련영어")
                    .userId("user1@aaa.com")
                    .bookmark(random.nextBoolean())
                    .mistakes(random.nextInt(10)+1)
                    .section(i%vocaList2Sec+1)
                    .build();
            vocaList2.add(voca);
        }
        vocaRepository.saveAll(vocaList1);
        vocaRepository.saveAll(vocaList2);

    }
//    @Test
    public void testReadVoca(){
    }
    @Test
    public void testUpdateVoca(){
        String title="테스트제목1";
        String userId="user1@aaa.com";
        List<String> updateEng =new ArrayList<>();
        List<String> updateKor = new ArrayList<>();
        for(int i=0;i<=3;i++){
            updateEng.add("engUpdateTest"+i);
            updateKor.add("한글업데이트테스트"+i);
        }
        vocaRepository.deleteByUserIdAndTitle(userId,title);

        List<Voca> saveList=new ArrayList<>();
        for(int i=0; i<=3; i++){
            Voca voca = Voca.builder()
                    .eng(updateEng.get(i))
                    .kor(updateKor.get(i))
                    .title("테스트업데이트제목1")
                    .userId("user1@aaa.com")
                    .build();
            saveList.add(voca);
        }
        vocaRepository.saveAll(saveList);



    }
    @Test
    public void testDeleteVoca(){
        String userId="user1@aaa.com";
        String title="테스트업데이트제목1";
        vocaRepository.deleteByUserIdAndTitle(userId,title);
    }

}
