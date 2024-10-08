package org.intelli.intellimentor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.QuizItemDTO;
import org.intelli.intellimentor.dto.QuizRequestDTO;
import org.intelli.intellimentor.dto.Voca.VocaAllDTO;
import org.intelli.intellimentor.dto.Voca.VocaItemDTO;
import org.intelli.intellimentor.dto.Voca.VocaSectionDTO;
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
import java.util.stream.Collectors;

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
    @Transactional
    @Commit
    public void testGetLearn(){
        //초기 데이터 세팅
        Long titleId=2L;

        //로직
        Voca voca = vocaRepository.findFirstByTitleId(titleId);
        List<Long> sectionIdList = vocaRepository.getSectionList(titleId); //3,4,5
        VocaAllDTO vocaAllDTO = new VocaAllDTO();
        List<VocaSectionDTO> sectionList = new LinkedList<>();

        for(Long sectionId : sectionIdList){
            List<Voca> vocaList = vocaRepository.findBySectionIdOrderById(sectionId);
            VocaSectionDTO vocaSectionDTO = new VocaSectionDTO();
            List<VocaItemDTO> wordList = new LinkedList<>();
            for(Voca row : vocaList){
                VocaItemDTO vocaItemDTO = new VocaItemDTO();
                vocaItemDTO.setId(row.getId());
                vocaItemDTO.setEng(row.getEng());
                vocaItemDTO.setKor(row.getKor());
                vocaItemDTO.setBookmark(row.isBookmark());
                vocaItemDTO.setMistakes(row.getMistakes());
                wordList.add(vocaItemDTO);
            }
            vocaSectionDTO.setVocaItemDTOS(wordList);
            Section section = sectionRepository.findById(sectionId).orElseThrow();

            vocaSectionDTO.setSectionId(section.getId());
            vocaSectionDTO.setSection(section.getSection());
            vocaSectionDTO.setProgress(section.getProgress());
            vocaSectionDTO.setGrade(section.getGrade());
            sectionList.add(vocaSectionDTO);
        }
        vocaAllDTO.setTitleId(titleId);
        vocaAllDTO.setTitle(voca.getTitle().getTitle());
        vocaAllDTO.setVocaSectionDTOs(sectionList);
        log.info(vocaAllDTO);

    }

    //학습하기
    @Test
    @Transactional
    @Commit
    public void getLearnBySection(){
        //초기 데이터 세팅
        Long sectionId = 5L;

        //로직
        List<Voca> vocaList = vocaRepository.findBySectionIdOrderById(sectionId);
        VocaSectionDTO vocaSectionDTO = new VocaSectionDTO();
        List<VocaItemDTO> wordList = new LinkedList<>();
        List<Voca> createSentenceList=new ArrayList<>();

        //문장 생성 레코드 필터링
        for(Voca row: vocaList){
            if(row.getSentenceEng()==null || row.getSentenceKor()==null){
                createSentenceList.add(row);
            }
        }
        //문장 생성
        if(!createSentenceList.isEmpty()){
            testCreateSentence(createSentenceList);
        }

        //데이터 출력
        for(Voca row : vocaList){
            VocaItemDTO vocaItemDTO = VocaItemDTO.fromEntity(row);
            wordList.add(vocaItemDTO);
        }
        vocaSectionDTO.setVocaItemDTOS(wordList);

        Section section = sectionRepository.findById(sectionId).orElseThrow();

        vocaSectionDTO.setSectionId(section.getId());
        vocaSectionDTO.setSection(section.getSection());
        vocaSectionDTO.setProgress(section.getProgress());
        log.info(vocaSectionDTO);

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
        Long sectionId = 5L;
        String subject = "eks";

        List<Voca> vocaList = vocaRepository.getVocaBySectionId(sectionId);
        List<List<Map<String, Object>>> result = new ArrayList<>();

        subject.chars().distinct().forEach(ch -> {
            char type = (char) ch;
            if (type == 'e' || type == 'k') {
                for (Voca row : vocaList) {
                    List<Map<String, Object>> temList = testFindChoices(vocaList, row, String.valueOf(type));
                    result.add(temList);
                }
            } else if (type == 's') {
                List<Voca> createSentenceList = vocaList.stream()
                        .filter(row -> row.getSentenceEng() == null || row.getSentenceKor() == null)
                        .collect(Collectors.toList());

                // 문장 생성
                if (!createSentenceList.isEmpty()) {
                    testCreateSentence(createSentenceList);
                }

                // 문장 퀴즈 생성
                for (Voca row : vocaList) {
                    List<Map<String, Object>> temList = testFindChoices(vocaList, row, "s");
                    result.add(temList);
                }
            }
        });

        log.info("quiz: {}", result);
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
            } else if (score >= vocaCount*0.65) {
                grade = "C";
                if(section.getSenScore()>=vocaCount*0.65) grade+="+";
            } else if (score >= vocaCount*0.4) {
                grade = "D";
                if(section.getSenScore()>=vocaCount*0.4) grade+="+";
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

        result.put("countMap",countMap);
        result.put("vocaCount",section.getVocaCount());
        result.put("countEng",section.getEngScore());
        result.put("countKor",section.getKorScore());
        result.put("countSen",section.getSenScore());
        result.put("progress",section.getProgress());
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

    private List<Map<String, Object>> testFindChoices(List<Voca> listVoca, Voca mainVoca, String type) {
        List<Voca> filteredList = new ArrayList<>(listVoca);
        filteredList.remove(mainVoca);
        Collections.shuffle(filteredList);

        if (type.equals("e") || type.equals("k")) {
            String mainField = type.equals("e") ? "eng" : "kor";
            String subField = type.equals("e") ? "kor" : "eng";
            return generateWordChoices(filteredList, mainVoca, mainField, subField);
        } else {
            return generateSentenceChoices(filteredList, mainVoca);
        }
    }

    private List<Map<String, Object>> generateWordChoices(List<Voca> filteredList, Voca mainVoca, String mainField, String subField) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(Map.of("id", mainVoca.getId(), mainField, mainField.equals("eng") ? mainVoca.getEng() : mainVoca.getKor()));
        filteredList.subList(0, 3).forEach(voca -> resultList.add(Map.of("id", voca.getId(), subField, subField.equals("kor") ? voca.getKor() : voca.getEng())));
        int randomInt = (int) (Math.random() * 4) + 1;
        resultList.add(randomInt, Map.of("id", mainVoca.getId(), subField, subField.equals("kor") ? mainVoca.getKor() : mainVoca.getEng()));
        return resultList;
    }

    private List<Map<String, Object>> generateSentenceChoices(List<Voca> filteredList, Voca mainVoca) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String sentence = mainVoca.getSentenceEng().replaceAll("(?i)" + mainVoca.getEng(), "__________");
        resultList.add(Map.of("id", mainVoca.getId(), "sentence", sentence));
        filteredList.subList(0, 3).forEach(voca -> resultList.add(Map.of("id", voca.getId(), "eng", voca.getEng())));
        int randomInt = (int) (Math.random() * 4) + 1;
        resultList.add(randomInt, Map.of("id", mainVoca.getId(), "eng", mainVoca.getEng()));
        return resultList;
    }


        @Transactional
        @Commit
        protected void testCreateSentence(List<Voca> createVocaList){
            StringBuilder system = new StringBuilder();
            system.append("사족 붙히지 말고 원하는 답만 알려줘\n")
                    .append("사용자는 Map의 형태로 <단어:단어뜻>을 알려줄거야.\n")
                    .append("너는 단어가 들어간 문장과 문장의 뜻을 알려줘\n")
                    .append("정답은 LinkedHashMap형태로 {\"단어\":\"문장/문장뜻\"} 이렇게 알려줘");
            StringBuilder prompt= new StringBuilder();
            for(Voca row:createVocaList){
                prompt.append(row.getEng()).append(":").append(row.getKor()).append(",");
            }
            log.info("prompt: "+prompt);
            String response = testChatGPT(prompt.toString(),system.toString());

            ObjectMapper mapper = new ObjectMapper();
            try {
                // String을 Map으로 변환
                Map<String, String> map = mapper.readValue(response, Map.class);
                int i =0;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String value = entry.getValue();
                    String[] splitValues = value.split("/");
                    createVocaList.get(i).setSentenceEng(splitValues[0]);
                    createVocaList.get(i).setSentenceKor(splitValues[1]);

                    log.info("Key: " + entry.getKey() + ", Value: " + value);
                    i++;
                }
                vocaRepository.saveAllAndFlush(createVocaList);

            } catch (Exception e) {
                e.printStackTrace();
            }



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
