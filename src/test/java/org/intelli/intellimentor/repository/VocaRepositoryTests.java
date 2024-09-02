package org.intelli.intellimentor.repository;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Title;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaUpdateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
public class VocaRepositoryTests {
    @Autowired
    private VocaRepository vocaRepository;
    @Autowired
    private TitleRepository titleRepository;
    @Autowired
    private SectionRepository sectionRepository;

    @Test
    public void testInsertVoca(){
        vocaRepository.deleteAll();
        titleRepository.deleteAll();
        sectionRepository.deleteAll();
        List<Voca> vocaList1 = new ArrayList<>();
        List<Voca> vocaList2 = new ArrayList<>();
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
        Title title1 = Title.builder().title("토익").build();
        Title title2 = Title.builder().title("여행관련영어").build();
        titleRepository.save(title1);
        titleRepository.save(title2);

        //섹션설정
        int sectionMax = 3;
        List<Long> sectionIdList = new ArrayList<>();
        for(int i =1; i<=sectionMax;i++){
            Section section = Section.builder().section(i).build();
            sectionRepository.save(section);
            sectionIdList.add(section.getId());
        }

        for(int i=0; i<englishWords1.length; i++){
            Long sectionId = sectionIdList.get(i % sectionMax);

            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new RuntimeException("Section not found"));
            Voca voca = Voca.builder()
                    .eng(englishWords1[i])
                    .kor(meanings1[i])
                    .title(title1)
                    .userId("user1@aaa.com")
                    .section(section)
                    .bookmark(random.nextBoolean())
                    .mistakes(random.nextInt(10)+1)
                    .build();
            vocaList1.add(voca);
        }
        for(int i=0;i< englishWords2.length;i++){
            Voca voca = Voca.builder()
                    .eng(englishWords2[i])
                    .kor(meanings2[i])
                    .title(title2)
                    .userId("user1@aaa.com")
                    .bookmark(random.nextBoolean())
                    .mistakes(random.nextInt(10)+1)
                    .build();
            vocaList2.add(voca);
        }
        vocaRepository.saveAll(vocaList1);
        vocaRepository.saveAll(vocaList2);
    }
    @Test
    public void testGetVocaList(){
        String email = "user1@aaa.com";
        List<Object[]> vocaList = vocaRepository.getVocaList(email);

        List<Map<String,Object>> resultList = new ArrayList<>();
        for(Object[] row : vocaList){
            Map<String,Object> vocaListMap = new LinkedHashMap<>();
            vocaListMap.put("title_id",row[0]);
            vocaListMap.put("title",row[1]);
            vocaListMap.put("count",row[2]);
            vocaListMap.put("section",row[3]);

            resultList.add(vocaListMap);
        }
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("data",resultList);

        log.info(result);
    }
    @Test
    public void testGetVocaDetails(){
        Long titleId = 1L; //토익

        String title = titleRepository.getTitle(titleId);
        List<Object[]> vocaList = vocaRepository.getVocaListDetails(titleId);


        List<Map<String,Object>> wordList = new ArrayList<>();
        for(Object[] row : vocaList){
            Map<String,Object> vocaListMap = new LinkedHashMap<>();
            vocaListMap.put("id",row[0]);
            vocaListMap.put("eng",row[1]);
            vocaListMap.put("kor",row[2]);

            wordList.add(vocaListMap);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("titleId",titleId);
        result.put("title",title);
        result.put("word",wordList);

        log.info(result);

    }
    @Test
    public void testUpdateVoca(){
        String email = "user1@aaa.com";

        VocaUpdateDTO vocaUpdateDTO = new VocaUpdateDTO();
        vocaUpdateDTO.setTitleId(1L);
        vocaUpdateDTO.setModifiedTitle("토익수정");
        Title title = titleRepository.findById(vocaUpdateDTO.getTitleId())
                .orElseThrow(() -> new RuntimeException("Title not found"));
        if(vocaUpdateDTO.getModifiedTitle()!=null){
            title.setTitle(vocaUpdateDTO.getModifiedTitle());
            titleRepository.save(title);
        }
        //수정할 단어
        Voca modifiedWord = Voca.builder()
                .id(1L)
                .userId(email)
                .eng("apple")
                .kor("사과")
                .build();
        List<Voca> modifiedWordList = new ArrayList<>();
        modifiedWordList.add(modifiedWord);
        vocaUpdateDTO.setModifiedWord(modifiedWordList);

        //단어를 꺼내 title 설정
//        List<Voca> modifiedWordList = vocaUpdateDTO.getModifiedWord();
        for (Voca voca : modifiedWordList) {
            voca.setTitle(title);
        }

        //삭제할 단어
        List<Long> deleteId = new ArrayList<>();
        deleteId.add(3L);
        vocaUpdateDTO.setDeleteId(deleteId);

        //추가될 단어
        Voca addWord = Voca.builder()
                .eng("banana")
                .kor("바나나")
                .userId(email)
                .title(title)
                .build();
        List<Voca> addWordList = new ArrayList<>();
        addWordList.add(addWord);
        vocaUpdateDTO.setAddWord(addWordList);

        //비어있다면 빈 객체로 초기화
        List<Voca> modifiedList = vocaUpdateDTO.getModifiedWord() != null ? vocaUpdateDTO.getModifiedWord() : new ArrayList<>();

        List<Long> deleteList = vocaUpdateDTO.getDeleteId();
        List<Voca> addList = vocaUpdateDTO.getAddWord();

        //변경할 데이터가 있으면 section 초기화
        if(modifiedList.isEmpty()|| deleteList.isEmpty()|| addList.isEmpty()){
            List<Long> sectionIdList = vocaRepository.findDistinctSectionIds(vocaUpdateDTO.getTitleId());
            sectionIdList = sectionIdList.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!sectionIdList.isEmpty()) {
                vocaRepository.resetSection(vocaUpdateDTO.getTitleId());
                sectionRepository.deleteAllById(sectionIdList);
            }


        }

        if (modifiedList != null && !modifiedList.isEmpty()) {
            vocaRepository.saveAll(modifiedList);
        }

        if(deleteList != null && !deleteList.isEmpty()){
            vocaRepository.deleteAllById(vocaUpdateDTO.getDeleteId());
        }
        if(addList !=null && !addList.isEmpty()){
            vocaRepository.saveAll(vocaUpdateDTO.getAddWord());
        }

        // 검증
        Voca updatedVoca = vocaRepository.findById(1L).orElse(null);
        assertNotNull(updatedVoca);
        assertEquals("apple", updatedVoca.getEng());
        assertEquals("사과", updatedVoca.getKor());

        assertFalse(vocaRepository.existsById(2L)); // 삭제된 단어가 존재하지 않음을 확인

        List<Voca> addedVoca = vocaRepository.findByEng("banana");
        assertEquals(1, addedVoca.size());
        assertEquals("바나나", addedVoca.get(0).getKor());
    }

    @Test
    public void testDeleteVoca(){
        Long titleId = 2L;
        titleRepository.deleteById(titleId);
    }

}
