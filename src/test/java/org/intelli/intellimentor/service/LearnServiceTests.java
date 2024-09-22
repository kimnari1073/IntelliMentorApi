package org.intelli.intellimentor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.QuizItemDTO;
import org.intelli.intellimentor.dto.QuizRequestDTO;
import org.intelli.intellimentor.repository.SectionRepository;
import org.intelli.intellimentor.repository.VocaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.Commit;
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

    //섹션 초기화
    @Test
    public void testResetSection(){
        Long titleId = 2L;

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

    //학습 조회(전체)
    @Test
    public void testGetLearn(){
        //초기 데이터 세팅
        Long titleId=2L;

        //로직
        Voca voca = vocaRepository.findFirstByTitleId(titleId);
        List<Long> sectionIdList = vocaRepository.getSectionList(titleId);
        Map<String,Object> resultMap = new LinkedHashMap<>();

        List<Map<String,Object>> dataList = new LinkedList<>();
        //3,4,5
        for(Long sectionId:sectionIdList){
            dataList.add(testGetSectionData(sectionId));
        }

        resultMap.put("title",voca.getTitle());
        resultMap.put("sectionMax",sectionIdList.size());
        resultMap.put("data",dataList);

        log.info(resultMap);
    }

    //학습 조회(섹션별)
    @Test
    @Transactional
    @Commit
    public void getLearnBySection(){
        //초기 데이터 세팅
        Long sectionId = 5L;

        //로직
        testGetSectionData(sectionId);
    }


    //북마크
    @Test
    public void setBookmark(){
        //초기 데이터 세팅
        Long vocaId = 10L;

        //로직
        Optional<Voca> voca = vocaRepository.findById(vocaId);

        if(voca.isPresent()){
            log.info("before: "+voca.get().isBookmark());
            voca.get().setBookmark(!voca.get().isBookmark());
            vocaRepository.save(voca.get());
            log.info("after: "+voca.get().isBookmark());

        }else{
            log.info("에러");
        }
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

                result.add(temList);
            }
        }
        if (subject.contains("k")) {
            for (Voca row : vocaList) {
                List<Map<String, Object>> temList = testFindChoices(vocaList, row, "k");

                result.add(temList);
            }
        }
        if(subject.contains("e")){

        }

        log.info("quiz: " + result);
    }

    //퀴즈 채점
    @Test
    public void testMarkQuiz(){
        //값 세팅
        QuizRequestDTO quizRequestDTO = new QuizRequestDTO();
        Long sectionId = 5L;
        List<QuizItemDTO> requestList = new ArrayList<>();  // DTO 리스트 생성

        boolean flag = true;
        for (int intI = 0; intI < 2; intI++) {
            for (Long i = 0L; i < 10L; i++) {
                QuizItemDTO addItem = new QuizItemDTO();
                addItem.setId(90L + i);  // id 설정

                if (flag) {
                    addItem.setType("e");  // 영어일 경우
                } else {
                    addItem.setType("k");  // 한국어일 경우
                }

                addItem.setCorrect(true);  // correct 설정
                requestList.add(addItem);  // 리스트에 추가
                flag = !flag;  // flag 토글
            }
        }

        // QuizRequestDTO에 requestList 설정
        quizRequestDTO.setData(requestList);

        //로직
        Map<String, Integer> countMap = new LinkedHashMap<>();
        countMap.put("e",null);
        countMap.put("k",null);
        countMap.put("s",null);
        List<Long> incorrectList = new LinkedList<>();

        for (QuizItemDTO row : quizRequestDTO.getData()) {
            String type = row.getType();

            if (row.getCorrect()) {
                countMap.put(type, countMap.get(type) == null ? 1 : countMap.get(type) + 1);
            } else {
                incorrectList.add(row.getId());  // 오답 리스트
                countMap.putIfAbsent(type, 0);  // null 일 경우 0으로 초기화
            }
        }

        log.info("countMap: "+countMap);



        //점수 업데이트 (백분율)
        Section section = sectionRepository.findById(sectionId).orElseThrow();

        countMap.entrySet().removeIf(entry -> entry.getValue() == null);// null 값인 경우 해당 키 삭제

        section.setEngScore(countMap.getOrDefault("e", section.getEngScore()));
        section.setKorScore(countMap.getOrDefault("k", section.getKorScore()));
        section.setSenScore(countMap.getOrDefault("s", section.getSenScore()));

        //grade계산
        String grade = null;
        if(section.getEngScore()==null||section.getKorScore()==null){
            grade = "-";
        }else{
            int score = (section.getEngScore()+section.getKorScore())/2;
            int vocaCount = section.getVocaCount();
            // 기본 등급 설정
            if (score >= vocaCount*0.9) {
                grade = "A";
                if(section.getSenScore()>=vocaCount*0.9) grade+="+";
            } else if (score >= vocaCount*0.8) {
                grade = "B";
                if(section.getSenScore()>=vocaCount*0.8) grade+="+";
            } else if (score >= vocaCount*0.7) {
                grade = "C";
                if(section.getSenScore()>=vocaCount*0.7) grade+="+";
            } else if (score >= vocaCount*0.6) {
                grade = "D";
                if(section.getSenScore()>=vocaCount*0.96) grade+="+";
            } else {
                grade = "F";
            }
        }


        section.setGrade(grade);
        log.info("grade: "+section.getGrade());

        //진행률
        int progress = Optional.ofNullable(section.getEngScore()).orElse(0) +
                Optional.ofNullable(section.getKorScore()).orElse(0) +
                Optional.ofNullable(section.getSenScore()).orElse(0);

        // `section.getProgress()` 값과 `progress`를 비교하여 더 큰 값을 설정
        section.setProgress(Math.max(progress, Optional.ofNullable(section.getProgress()).orElse(0)));

        //섹션 저장
        sectionRepository.save(section);

        //mistakes 필드 수정 로직
        //결과 출력용
        List<Map<String,Object>> misList = new ArrayList<>();
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
        Map<String, Integer> scoreMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            String type = entry.getKey();
            Integer count = entry.getValue();
            int score = (int) (((double) count / section.getVocaCount()) * 100);
            scoreMap.put(type,score);
        }
        result.put("countMap",countMap);
        result.put("scoreMap",scoreMap);
        result.put("vocaCount",section.getVocaCount());
        result.put("countEng",section.getEngScore());
        result.put("countKor",section.getKorScore());
        result.put("countSen",section.getSenScore());
        result.put("scoreEng",(int) (((double) section.getEngScore() / section.getVocaCount()) * 100));
        result.put("scoreKor",(int) (((double) section.getKorScore() / section.getVocaCount()) * 100));
        result.put("scoreSen",(int) (((double) section.getSenScore() / section.getVocaCount()) * 100));
        result.put("grade",section.getGrade());
        result.put("mistakes",misList);
        log.info(result);

    }



    //chatGPT 연결 테스트
    @Test
    public void generateChatResponse() {
    String prompt = "perseverance의 뜻은 인내야. 이 단어가 들어간 영어 예문을 한 문장만 만들어줘";
    String system = "사족 붙히지 말고 원하는 답만 알려줘.\n" +
            "정답은 영어문장/n문장뜻 형식으로 알려줘.\n" +
            "문장의 길이는 100자가 넘지 않게 해줘.";
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
            ResponseEntity<String> responseEntity = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class);

            // 응답 처리
            String responseBody = responseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseBody);
            String chatResponse = jsonNode.path("choices").get(0).path("message").path("content").asText();


            log.info("chatResponse: "+chatResponse);
            String[] sentences = chatResponse.split("/");

            String sentence1 = sentences[0].trim(); // 첫 번째 문장
            String sentence2 = sentences[1].trim(); // 두 번째 문장

            log.info("Sentence 1: " + sentence1);
            log.info("Sentence 2: " + sentence2);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error occurred: "+e.getMessage());
        }
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

            List<Voca> createSentenceList=vocaRepository.findBySectionIdAndSentenceEngIsNull(sectionId);
            testCreateSentence(createSentenceList);
            for(Voca row : vocaList){
                Map<String, Object> wordMap = new LinkedHashMap<>();
                wordMap.put("id",row.getId());
                wordMap.put("eng", row.getEng());
                wordMap.put("kor", row.getKor());
                wordMap.put("bookmark", row.isBookmark());
                wordMap.put("mistakes", row.getMistakes());
                wordMap.put("sentenceEng",row.getSentenceEng());
                wordMap.put("sentenceKor",row.getSentenceKor());
                wordList.add(wordMap);
            }

            resultMap.put("section",vocaList.get(0).getSection());
            resultMap.put("wordList",wordList);

            return resultMap;
        }


        @Transactional
        @Commit
        protected void testCreateSentence(List<Voca> createVocaList){
            log.info("testCreateSentence");
            String system = "사족 붙히지 말고 원하는 답만 알려줘.\n" +
                    "정답은 영어문장/n문장뜻 형식으로 알려줘.\n" +
                    "문장의 길이는 100자가 넘지 않게 해줘.";
            for(Voca row:createVocaList){
                String prompt = row.getEng()+"의 뜻은 "+row.getKor()+" 야. 이 단어가 들어간 영어 예문을 한 문장만 만들어줘";
                log.info("prompt: "+prompt);
                String response = testChatGPT(prompt,system);
                String[] sentences = response.split("/");

                row.setSentenceEng(sentences[0].trim());
                row.setSentenceKor(sentences[1].trim());
                log.info("eng: "+row.getSentenceEng());
                log.info("kor: "+row.getSentenceKor());
            }
            vocaRepository.saveAllAndFlush(createVocaList);

        }
        private String testChatGPT(String prompt,String system){
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
                ResponseEntity<String> responseEntity = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class);

                // 응답 처리
                String responseBody = responseEntity.getBody();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(responseBody);
                String chatResponse = jsonNode.path("choices").get(0).path("message").path("content").asText();

                return chatResponse;
            } catch (Exception e) {
                e.printStackTrace();
                log.info("Error occurred: "+e.getMessage());
                return null;
            }

        }
}
