package org.intelli.intellimentor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
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

    //학습 조회
    @Test
    public void testGetLearn(){
        Long titleId=1L;

        List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(titleId);
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
        //값 세팅
        Long sectionId = 5L;
        Map<String, Object> wrappedRequest = new LinkedHashMap<>();  // 최상위 맵 생성
        List<Map<String, Object>> requestList = new ArrayList<>();   // 리스트 생성

        boolean flag = true;
        for (int intI = 0; intI < 2; intI++) {
            for (Long i = 0L; i < 10L; i++) {
                Map<String, Object> addList = new LinkedHashMap<>();

                addList.put("id", 90L + i);
                if (flag) {
                    addList.put("type", "e");
                } else {
                    addList.put("type", "k");
                }
                addList.put("correct", flag);
                requestList.add(addList);
                flag = !flag;
            }
        }

        wrappedRequest.put("data", requestList);  // 맨 마지막에 requestList를 최상위 맵에 추가

        log.info("request: "+ requestList);

        //로직
        Map<String, Integer> scoreMap = new LinkedHashMap<>();
        scoreMap.put("e",null);
        scoreMap.put("k",null);
        scoreMap.put("s",null);
        List<Long> incorrectList = new LinkedList<>();

        for (Map<String, Object> row : requestList) {
            String type = (String) row.get("type");

            if ((boolean) row.get("correct")) {
                switch (type) {
                    case "e":
                        scoreMap.put("e", scoreMap.get("e") == null ? 1 : scoreMap.get("e") + 1);
                        break;
                    case "k":
                        scoreMap.put("k", scoreMap.get("k") == null ? 1 : scoreMap.get("k") + 1);
                        break;
                    case "s":
                        scoreMap.put("s", scoreMap.get("s") == null ? 1 : scoreMap.get("s") + 1);
                        break;
                }
            } else {
                incorrectList.add((Long) row.get("id"));
                scoreMap.putIfAbsent((String) row.get("type"), 0);  // null인 경우 0으로 초기화

            }
        }
        log.info("scoreMap: "+scoreMap);



        //점수 업데이트 (백분율)
        Section section = sectionRepository.findById(sectionId).orElseThrow();
        Iterator<Map.Entry<String, Integer>> iterator = scoreMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();

            if (entry.getValue() == null) {
                iterator.remove();  // null 값인 경우 해당 키 삭제
            } else {
                int score = (int) ((double) entry.getValue() / section.getVocaCount() * 100);
                scoreMap.put(entry.getKey(), score);  // 점수 업데이트
            }
        }
//        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
//            if(entry.getValue()!=null){
//                int score = (int) ((double) entry.getValue() / section.getVocaCount() * 100);
//
//                scoreMap.put(entry.getKey(), score);
//            }
//        }
        log.info("현재 점수 scoreMap: "+scoreMap);

        section.setEngScore(scoreMap.getOrDefault("e", section.getEngScore()));
        section.setKorScore(scoreMap.getOrDefault("k", section.getKorScore()));
        section.setSenScore(scoreMap.getOrDefault("s", section.getSenScore()));

        log.info("원래 점수 scoreMap: ");

        //grade계산
        String grade = null;
        if(section.getEngScore()==null||section.getKorScore()==null){
            log.info("점수 없음");
            grade = "-";
        }else{
            int score = (section.getEngScore()+section.getKorScore())/2;
            // 기본 등급 설정
            if (score >= 90) {
                grade = "A";
            } else if (score >= 80) {
                grade = "B";
            } else if (score >= 70) {
                grade = "C";
            } else if (score >= 60) {
                grade = "D";
            } else {
                grade = "F";
            }

            // + 등급 설정
            if(section.getSenScore()!=null){
                if (score >= 90 && section.getSenScore() >= 90) {
                    grade += "+";
                } else if (score >= 80 && section.getSenScore() >= 80) {
                    grade += "+";
                } else if (score >= 70 && section.getSenScore() >= 70) {
                    grade += "+";
                } else if (score >= 60 && section.getSenScore() >= 60) {
                    grade += "+";
                }
            }
        }
        section.setGrade(grade);
        log.info("grade: "+section.getGrade());
        sectionRepository.save(section);


        //결과 출력용
        List<Map<String,Object>> misList = new ArrayList<>();
        Map<String,Object> misMap = new LinkedHashMap<>();

        //mistakes 필드 수정 로직
        List<Voca> mistakesList = vocaRepository.findAllById(incorrectList);
        for (Voca row : mistakesList) {
            int frequency = Collections.frequency(incorrectList, row.getId());  // incorrectList에서 해당 ID의 빈도수 계산
            row.setMistakes(row.getMistakes() + frequency);
            //반환용 데이터
            misMap.put("id",row.getId());
            misMap.put("eng",row.getEng());
            misMap.put("kor",row.getKor());
            misList.add(misMap);
        }
        vocaRepository.saveAll(mistakesList);


        //countMap - 틀린 단어
        //
//        퀴즈 점수 scoreMap
//        총 점수 section.getScore
//                grade
//        오답리스트 countMap
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scoreMap",scoreMap);
        result.put("scoreEng",section.getEngScore());
        result.put("scoreKor",section.getKorScore());
        result.put("scoreSen",section.getSenScore());
        result.put("grade",section.getGrade());
        result.put("mistakes",misList);



        log.info(result);

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
