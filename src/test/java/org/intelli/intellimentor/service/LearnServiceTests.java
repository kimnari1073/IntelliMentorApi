package org.intelli.intellimentor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.repository.SectionRepository;
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
public class LearnServiceTests {

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private VocaRepository vocaRepository;
    //섹션 설정
    @Test
    public void testSetSection() {
        Long titleId = 6L;
        int requestSection = 3;
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
        log.info("Voca save..");
    }

    //섹션 초기화
    @Test
    public void testResetSection(){
        Long titleId = 2L;

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

    //학습 조회
    @Test
    public void testGetLearn(){
        Long titleId=1L;

        List<Voca> vocaList = vocaRepository.getVocaListDetails(titleId);
        vocaList.sort(Comparator.comparing(voca -> voca.getSection().getSection()));
        List<Map<String, Object>> wordList = new ArrayList<>();
        List<Map<String, Object>> sectionsList = new ArrayList<>();

        int i=1;
        for (Voca row : vocaList) {
            if (row.getSection().getSection() != i) {
                // 이전 섹션의 데이터를 sectionsList에 추가
                Map<String, Object> sections = new LinkedHashMap<>();
                sections.put("sectionId",row.getSection().getId());
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
            wordMap.put("id",row.getId());
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
            sections.put("sectionId",vocaList.get(vocaList.size() - 1).getSection().getId());
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
        log.info(resultData);
    }

    //북마크(대시보드)
    @Test
    public void setBookmark(){
        Long titleId = 1L;
        List<Long> trueIdList=new ArrayList<>();
//        List<Long> falseIdList=new ArrayList<>();
        trueIdList.add(1L);
        trueIdList.add(2L);
//        falseIdList.add(3L);
//        falseIdList.add(4L);
        List<Voca> vocaList1 = vocaRepository.getVocaByTitleAndIdIn(titleId,trueIdList);
        log.info("before: "+vocaList1.get(0).isBookmark());
        for(Voca row:vocaList1){
            row.setBookmark(true);
        }
        vocaRepository.saveAll(vocaList1);
        log.info("after: "+vocaList1.get(0).isBookmark());

    }

    //퀴즈 생성(Eng)
    //Map<String,List<Map<String,Object>>
    @Test
    public void testGetQuizEng(){
        Long sectionId =15L;

        List<Voca> vocaList = vocaRepository.getVocaBySectionId(sectionId);
        log.info("vocaList: " +vocaList);
        List<Map<String,Object>> result = new ArrayList<>();
        int quizNumber = 1;
        for(Voca row:vocaList){

            List<Map<String,Object>> temList = new ArrayList<>();
            List<Voca> choices = testFindChoices(vocaList,row);
            log.info(choices);
            for(Voca voca:choices){
                Map<String,Object> temMap = new HashMap<>();
                temMap.put("id",voca.getId());
                temMap.put("kor",voca.getKor());
                temList.add(temMap);
            }
            Collections.shuffle(temList);

            //메인 영어 단어 추가
            Map<String,Object> temMap = new HashMap<>();
            temMap.put("id",row.getId());
            temMap.put("eng",row.getEng());
            temList.add(0,temMap);


            Map<String, Object> linkedMap = new LinkedHashMap<>();
            linkedMap.put("quizNumber", quizNumber);
            linkedMap.put("quizList", temList);
            result.add(linkedMap);
            quizNumber++;

        }

        log.info("quiz: "+result);
    }

    @Value("${openai.api.key}")
    private String apiKey;
    private final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    //chatGPT 연결 테스트
    @Test
    public void generateChatResponse() {
    String prompt = "사과와 어감이 비슷하지만 의미는 다른 한글 단어3개를 알려줘";
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
                    Map.of("role", "system", "content", "답은 리스트 형태로 [\"정답1\",\"정답2\",\"정답3\"]과 같이 말해줘.\n"),
                    // 'user' role로 실제 사용자 입력 제공
                    Map.of("role", "user", "content", prompt)
            });

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<String> responseEntity = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class);

            // 응답 처리
            String responseBody = responseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseBody);
            String chatResponse = jsonNode.path("choices").get(0).path("message").path("content").asText();


            List<String> resultList = new ArrayList<>(
                    Arrays.asList(chatResponse.replace("[", "")
                            .replace("]", "")
                            .split(", ")));
            log.info("chatResponse: "+chatResponse);
            log.info("resultList: "+resultList);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error occurred: "+e.getMessage());
        }
    }
//
//    @Test
//    public void testUpdateLearn(){
//
//    }
//
//    @Test
//    public void testDeleteLearn(){
//
//    }
//
//    @Test
//    public void testCreateQuiz(){
//
//    }
        private List<Voca> testFindChoices(List<Voca> listVoca, Voca excludedVoca){
        List<Voca> filteredList = new ArrayList<>(listVoca);// 원본 리스트 복사

        filteredList.remove(excludedVoca);
        Collections.shuffle(filteredList);

        // 상위 3개 요소를 선택, 반환
        List<Voca> randomVocaList = filteredList.subList(0, Math.min(3, filteredList.size()));
        // 정답 추가
        randomVocaList.add(excludedVoca);
        return randomVocaList;
    }
}
