package org.intelli.intellimentor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Title;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.Voca.VocaDTO;
import org.intelli.intellimentor.dto.Voca.VocaUpdateDTO;
import org.intelli.intellimentor.repository.SectionRepository;
import org.intelli.intellimentor.repository.TitleRepository;
import org.intelli.intellimentor.repository.VocaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootTest
@Log4j2
public class VocaServiceTests {
    @Autowired
    private VocaRepository vocaRepository;
    @Autowired
    private TitleRepository titleRepository;
    @Autowired
    private SectionRepository sectionRepository;

    private final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${openai.api.key}")
    private String apiKey;

    @Test
    public void testInsertVoca() {
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
        int sectionMax = 2;
        List<Long> sectionIdList = new ArrayList<>();
        for (int i = 1; i <= sectionMax; i++) {
            Section section = Section.builder().section(i).build();
            sectionRepository.save(section);
            sectionIdList.add(section.getId());
        }

        for (int i = 0; i < englishWords1.length; i++) {
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
                    .mistakes(random.nextInt(10) + 1)
                    .build();
            vocaList1.add(voca);
        }
        for (int i = 0; i < englishWords2.length; i++) {
            Voca voca = Voca.builder()
                    .eng(englishWords2[i])
                    .kor(meanings2[i])
                    .title(title2)
                    .userId("user1@aaa.com")
                    .bookmark(random.nextBoolean())
                    .mistakes(random.nextInt(10) + 1)
                    .build();
            vocaList2.add(voca);
        }
        vocaRepository.saveAll(vocaList1);
        vocaRepository.saveAll(vocaList2);


        //섹션 설정
        Long titleId = 2L;
        int requestSection = 3;

        // 섹션 생성
        List<Section> saveSectionList = new ArrayList<>();
        for (int i = 1; i <= requestSection; i++) {
            Section section = Section.builder()
                    .section(i)
                    .build();
            saveSectionList.add(section);
        }

        // 섹션별 Voca 카운트 저장을 위한 배열 (각 섹션별로 카운트를 관리)
        int[] sectionVocaCount = new int[requestSection];

        // Voca 섹션 설정
        List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(titleId);
        int i = 0;
        for (Voca row : vocaList) {
            // 섹션을 할당
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

        sectionRepository.saveAll(saveSectionList);  // 섹션 저장
        vocaRepository.saveAll(vocaList);  // Voca 저장
    }

    @Test
    public void testGetVocaList() {
        String email = "user1@aaa.com";
        List<Object[]> vocaList = vocaRepository.getVocaList(email);

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object[] row : vocaList) {
            Map<String, Object> vocaListMap = new LinkedHashMap<>();
            vocaListMap.put("titleId", row[0]);
            vocaListMap.put("title", row[1]);
            vocaListMap.put("count", row[2]);
            vocaListMap.put("section", row[3]);

            resultList.add(vocaListMap);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", resultList);

        log.info(result);


    }

    @Test
    public void testGetVocaDetails() {
        Long titleId = 1L; //토익

        String title = titleRepository.getTitle(titleId);
        List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(titleId);


        List<Map<String, Object>> wordList = new ArrayList<>();
        for (Voca row : vocaList) {
            Map<String, Object> vocaListMap = new LinkedHashMap<>();
            vocaListMap.put("id", row.getId());
            vocaListMap.put("eng", row.getEng());
            vocaListMap.put("kor", row.getKor());

            wordList.add(vocaListMap);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("titleId", titleId);
        result.put("title", title);
        result.put("word", wordList);

        log.info(result);

    }

    @Test
    public void testUpdateVoca() {
        String email = "user1@aaa.com";
        Long titleId = 1L;
        //VocaUpdateDTO설정
        VocaUpdateDTO vocaUpdateDTO = new VocaUpdateDTO();
        vocaUpdateDTO.setModifiedTitle("토익수정");
        //수정할 단어
        Voca modifiedWord = Voca.builder()
                .id(1L)
                .eng("apple")
                .kor("사과")
                .build();
        List<Voca> modifiedWordList = new ArrayList<>();
        modifiedWordList.add(modifiedWord);
        vocaUpdateDTO.setModifiedWord(modifiedWordList);
        //삭제할 단어
        List<Long> deleteId = new ArrayList<>();
        deleteId.add(3L);
        vocaUpdateDTO.setDeleteId(deleteId);
        //추가될 단어
        Voca addWord = Voca.builder()
                .eng("banana")
                .kor("바나나")
                .build();
        List<Voca> addWordList = new ArrayList<>();
        addWordList.add(addWord);
        vocaUpdateDTO.setAddWord(addWordList);


        //영속성 컨텍스트로 관리되는 Title 객체
        Title title = titleRepository.findById(titleId)
                .orElseThrow(() -> new RuntimeException("Title not found"));

        List<Voca> modifiedList = vocaUpdateDTO.getModifiedWord();
        List<Long> deleteList = vocaUpdateDTO.getDeleteId();
        List<Voca> addList = vocaUpdateDTO.getAddWord();

        //title 수정
        if (vocaUpdateDTO.getModifiedTitle() != null &&
                !vocaUpdateDTO.getModifiedTitle().equals(title.getTitle())) {
            title.setTitle(vocaUpdateDTO.getModifiedTitle());
            titleRepository.save(title);
            log.info("Title Modified...");
        }

        //Section이 있다면 null로 설정
        List<Long> sectionList = vocaRepository.getSectionList(title.getId());
        log.info("sectionList: " + sectionList);
        log.info("modifiedList: " + modifiedList);
        log.info("deleteList: " + deleteList);
        log.info("addList: " + addList);
        if ((!modifiedList.isEmpty() || !deleteList.isEmpty() || !addList.isEmpty())
                && !sectionList.contains(null)) {

            //List<Voca> 조회 및 Section reset삭제
            List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(title.getId());
            for (Voca row : vocaList) {
                row.setSection(null);
                row.setMistakes(0);
                row.setBookmark(false);
            }
            vocaRepository.saveAll(vocaList);
            log.info("voca Save.");

            //Section 삭제
            sectionRepository.deleteAllById(sectionList);
            log.info("Section Delete..");
        }

        //수정
        if (!modifiedList.isEmpty()) {
            for (Voca voca : modifiedList) {
                voca.setUserId(email);
                voca.setTitle(title);
            }
            vocaRepository.saveAll(modifiedList);
            log.info("Voca Modified...");
        }

        //삭제
        if (!deleteList.isEmpty()) {
            vocaRepository.deleteAllById(deleteList);
            log.info("Voca Delete...");

        }

        //추가
        if (!addList.isEmpty()) {
            for (Voca voca : addList) {
                voca.setUserId(email);
                voca.setTitle(title);
            }
            vocaRepository.saveAll(addList);
            log.info("Voca Add...");

        }
    }

    @Test
    public void testDeleteVoca() {
        Long titleId = 2L;
        titleRepository.deleteById(titleId);
    }

    @Test
    public void testCreateVocaByChatGPT() throws JsonProcessingException {
        String subject = "해외여행";
        int count = 20;
        String email = "user1@aaa.com";
        String inputTitle="해외여행 시 필수 단어들";
        StringBuilder prompt = new StringBuilder();
        prompt.append(subject).append("과 관련된 단어 ").append(count).append("개 생성해줘");

        StringBuilder system = new StringBuilder();
        system.append("사족 붙히지 말고 원하는 답만 알려줘\n")
                .append("사용자가 원하는 주제와 관련된 단어를 생성해줘.\n")
                .append("Map 형태로 eng:[영어단어1,영어단어2...], kor:[영어단어의뜻1,영어단어의뜻2...]");

        Map<String, Object> message = testChatGPT(prompt.toString(), system.toString());

        String content = message.get("content").toString();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> map = mapper.readValue(content, new TypeReference<Map<String, List<String>>>(){});

        // eng와 kor 리스트 추출
        List<String> eng = map.get("eng");
        List<String> kor = map.get("kor");
        log.info("eng: "+eng);
        log.info("kor: "+kor);

        Title title = Title.builder().title(inputTitle).build();
        titleRepository.save(title);

        List<Voca> vocaList = new ArrayList<>();
        for(int i=0;i<eng.size();i++){
            Voca voca = Voca.builder()
                    .eng(eng.get(i))
                    .kor(kor.get(i))
                    .userId(email)
                    .title(title).build();
            vocaList.add(voca);
        }

        vocaRepository.saveAll(vocaList);
    }

    private Map<String, Object> testChatGPT(String prompt, String system) {
        try {
            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 요청 본문 작성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", new Object[]{
                    // 'system' role로 모델에 기본 지침 제공
                    Map.of("role", "system", "content", system),
                    // 'user' role로 실제 사용자 입력 제공
                    Map.of("role", "user", "content", prompt)
            });

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<Map> responseEntity = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);

            // 응답 처리
            Map<String, Object> responseBody = responseEntity.getBody();
            // choices 배열에서 첫 번째 요소 선택
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            return message;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error occurred: " + e.getMessage());
            return null;
        }

    }


}
