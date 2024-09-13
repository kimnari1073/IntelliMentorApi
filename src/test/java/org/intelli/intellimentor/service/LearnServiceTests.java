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
    private final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${openai.api.key}")
    private String apiKey;

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

    //퀴즈 생성
    @Test
    public void testGetQuiz(){
        Long sectionId = 25L;
        String subject = "eks";

        List<Voca> vocaList = vocaRepository.getVocaBySectionId(sectionId);
        List<List<Map<String, Object>>> result = new ArrayList<>(); // quizList가 아닌 List로 담음

        if (subject.contains("e")) {
            for (Voca row : vocaList) {
                List<Map<String, Object>> temList = testFindChoices(vocaList, row, "e");

                // 퀴즈 결과에 추가 (quizList라는 키 없이 바로 추가)
                result.add(temList);
            }
        }
        if (subject.contains("k")) {
            for (Voca row : vocaList) {
                List<Map<String, Object>> temList = testFindChoices(vocaList, row, "k");

                // 퀴즈 결과에 추가 (quizList라는 키 없이 바로 추가)
                result.add(temList);
            }
        }

        log.info("quiz: " + result);
    }

    //퀴즈 채점
    @Test
    public void testMarkQuiz(){
        Long sectionId = 25L;
        List<Long> quizList = List.of(95L, 88L, 101L, 104L, 107L);
        List<Long> answerList = List.of(95L,88L,100L,102L,102L);

        if(quizList.size()!=answerList.size()){
            log.info("에러에러");//에러
        }
        //sectionId=25L인 voca List
        List<Voca> vocaList = vocaRepository.getVocaBySectionId(sectionId);

        int score = 0;
        Set<Long> incorrectSet = new LinkedHashSet<>();
        for(int i = 0; i<answerList.size();i++){
            if(quizList.get(i).equals(answerList.get(i))){
                score++;
            }
            else {
                incorrectSet.add(quizList.get(i));
            }
        }
        log.info("score: "+score);
        log.info("incorrectSet: "+incorrectSet);


    }



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
}
