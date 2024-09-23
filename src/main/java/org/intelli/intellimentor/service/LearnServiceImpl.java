package org.intelli.intellimentor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Section;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.QuizItemDTO;
import org.intelli.intellimentor.dto.QuizRequestDTO;
import org.intelli.intellimentor.dto.VocaItemDTO;
import org.intelli.intellimentor.dto.VocaSectionDTO;
import org.intelli.intellimentor.repository.SectionRepository;
import org.intelli.intellimentor.repository.VocaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class LearnServiceImpl implements LearnService{
    private final VocaRepository vocaRepository;
    private final SectionRepository sectionRepository;
    private final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${openai.api.key}")
    private String apiKey;

    //섹션 설정
    @Override
    public void setSection(Long titleId, int requestSection) {
        //섹션 생성 (vocaCount = null)
        List<Section> saveSectionList = new ArrayList<>();
        for (int i = 1; i <= requestSection; i++) {
            Section section = Section.builder()
                    .section(i)
                    .build();
            saveSectionList.add(section);
        }

        // Voca 섹션 설정
        int[] sectionVocaCount = new int[requestSection];
        List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(titleId);
        int i = 0;
        for (Voca row : vocaList) {
            // 섹션 할당
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
        sectionRepository.saveAll(saveSectionList);
        vocaRepository.saveAll(vocaList);
    }
    //섹션 삭제
    @Override
    public void deleteLearn(Long titleId) {
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

    //북마크
    @Override
    public void setBookmark(Long vocaId) {
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

    //학습 조회(전체)
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLearn(Long titleId) {
        Voca voca = vocaRepository.findFirstByTitleId(titleId);
        List<Long> sectionIdList = vocaRepository.getSectionList(titleId);
        Map<String,Object> resultMap = new LinkedHashMap<>();

        List<Map<String,Object>> dataList = new LinkedList<>();

        for(Long sectionId:sectionIdList){
//            dataList.add(getSectionData(sectionId));
        }

        resultMap.put("title",voca.getTitle());
        resultMap.put("sectionMax",sectionIdList.size());
        resultMap.put("data",dataList);
        log.info(resultMap);
        return resultMap;
    }

    //학습 조회(섹션)
    @Override
    public VocaSectionDTO getLearnBySection(Long sectionId) {
        VocaSectionDTO vocaSectionDTO = getSectionData(sectionId);
        Section section = sectionRepository.findById(sectionId).orElseThrow();

        vocaSectionDTO.setSectionId(section.getId());
        vocaSectionDTO.setSection(section.getSection());
        vocaSectionDTO.setProgress(section.getProgress());
        return vocaSectionDTO;
    }

    //퀴즈 생성
    @Override
    public Map<String, Object> getQuiz(Long sectionId,String subject) {
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
        return Map.of("quiz",result);
    }

   //퀴즈 채점
    @Override
    public Map<String, Object> markQuiz(Long sectionId, QuizRequestDTO quizRequestDTO) {
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

        //진행률
        int progress = Optional.ofNullable(section.getEngScore()).orElse(0) +
                Optional.ofNullable(section.getKorScore()).orElse(0) +
                Optional.ofNullable(section.getSenScore()).orElse(0);
        section.setProgress(Math.max(progress, Optional.ofNullable(section.getProgress()).orElse(0)));

        //섹션 저장
        sectionRepository.save(section);

        //결과 출력용
        List<Map<String,Object>> misList = new ArrayList<>();

        //mistakes 필드 수정 로직
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

        return result;
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
    private VocaSectionDTO getSectionData(Long sectionId){
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
            createSentence(createSentenceList);
        }

        //데이터 출력
        for(Voca row : vocaList){
            VocaItemDTO vocaItemDTO = new VocaItemDTO();
            vocaItemDTO.setId(row.getId());
            vocaItemDTO.setEng(row.getEng());
            vocaItemDTO.setKor(row.getKor());
            vocaItemDTO.setBookmark(row.isBookmark());
            vocaItemDTO.setMistakes(row.getMistakes());
            vocaItemDTO.setSentenceEng(row.getSentenceEng());
            vocaItemDTO.setSentenceKor(row.getSentenceKor());
            wordList.add(vocaItemDTO);
        }


        vocaSectionDTO.setVocaItemDTOS(wordList);
        return vocaSectionDTO;
    }
    @Transactional
    public void createSentence(List<Voca> createVocaList){
        String system = "사족 붙히지 말고 원하는 답만 알려줘\n"+
                "사용자는 Map의 형태로 <단어:단어뜻>을 알려줄거야.\n" +
                "너는 단어가 들어간 문장과 문장의 뜻을 알려줘\n"+
                "정답은 LinkedHashMap형태로 {\"단어\":\"문장/문장뜻\"} 이렇게 알려줘";
        StringBuilder prompt= new StringBuilder();
        for(Voca row:createVocaList){
            prompt.append(row.getEng()).append(":").append(row.getKor()).append(",");
        }
        log.info("prompt: "+prompt);
        String response = getChatGPT(prompt.toString(),system);

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
    private String getChatGPT(String prompt,String system){
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
