package org.intelli.intellimentor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Title;
import org.intelli.intellimentor.dto.Voca.*;
import org.intelli.intellimentor.repository.SectionRepository;
import org.intelli.intellimentor.repository.TitleRepository;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.repository.VocaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class VocaServiceImpl implements VocaService{

    private final VocaRepository vocaRepository;
    private final TitleRepository titleRepository;
    private final SectionRepository sectionRepository;

    private final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${openai.api.key}")
    private String apiKey;

    @Override
    public VocaItemDTO getHomeVoca(String userId) {
        List<Voca> vocaList = vocaRepository.findByUserIdAndSectionIdIsNotNullAndSentenceEngIsNotNull(userId);
        List<Voca> topVocaList = vocaList.stream()
                .filter(voca -> voca.getMistakes() > 0) // mistakes 필드가 1 이상인 경우만 필터링
                .toList(); // 리스트로 변환

        VocaHomeDTO vocaHomeDTO;
        if (!topVocaList.isEmpty()) {
            // ThreadLocalRandom을 사용하여 랜덤하게 1개의 단어 선택
            Voca voca = topVocaList.get(ThreadLocalRandom.current().nextInt(topVocaList.size()));
            vocaHomeDTO = VocaHomeDTO.from(voca, voca.getSection().getId());
        } else { //틀린 단어가 없으면
            Voca voca = vocaList.get(ThreadLocalRandom.current().nextInt(vocaList.size()));
            vocaHomeDTO = VocaHomeDTO.from(voca, voca.getSection().getId());

        }
        return vocaHomeDTO;
    }
    //단어생성
    @Override
    public void createVoca(String email,VocaDTO vocaDTO) {//email,VocaDTO(title,kor,eng)
        Title title = Title.builder().title(vocaDTO.getTitle()).build();
        titleRepository.save(title);

        List<Voca> vocaList = new ArrayList<>();
        for (int i = 0; i < vocaDTO.getEng().size(); i++) {
            Voca voca = Voca.builder()
                    .userId(email)
                    .eng(vocaDTO.getEng().get(i))
                    .kor(vocaDTO.getKor().get(i))
                    .title(title)
                    .build();
            vocaList.add(voca);
        }
        vocaRepository.saveAll(vocaList);
    }

    @Override
    public void createVocaByAi(String email, VocaAiDTO vocaAiDTO) throws JsonProcessingException {
        StringBuilder prompt = new StringBuilder();
        prompt.append(vocaAiDTO.getSubject()).append("과 관련된 단어 ")
                .append(vocaAiDTO.getCount()).append("개 생성해줘");

        StringBuilder system = new StringBuilder();
        system.append("사족 붙히지 말고 원하는 답만 알려줘\n")
                .append("사용자가 원하는 주제와 관련된 단어를 생성해줘.\n")
                .append("Map 형태로 eng:[영어단어1,영어단어2...], kor:[영어단어의뜻1,영어단어의뜻2...]");

        Map<String, Object> message = chatGPT(prompt.toString(), system.toString());

        String content = message.get("content").toString();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> map = mapper.readValue(content, new TypeReference<Map<String, List<String>>>(){});

        // eng와 kor 리스트 추출
        List<String> eng = map.get("eng");
        List<String> kor = map.get("kor");
        log.info("eng: "+eng);
        log.info("kor: "+kor);

        Title title = Title.builder().title(vocaAiDTO.getTitle()).build();
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
    private Map<String, Object> chatGPT(String prompt, String system) {
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

    //단어 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public Map<String,Object> getVocaList(String email) {
        List<Object[]> vocaList = vocaRepository.getVocaList(email);

        List<Map<String,Object>> resultList = new ArrayList<>();
        for(Object[] row : vocaList){
            Map<String,Object> vocaListMap = new LinkedHashMap<>();
            vocaListMap.put("titleId",row[0]);
            vocaListMap.put("title",row[1]);
            vocaListMap.put("count",row[2]);
            vocaListMap.put("section",row[3]);

            resultList.add(vocaListMap);
        }
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("data",resultList);
        return result;
    }

    //단어 수정 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVocaListDetails(Long titleId) {
        String title = titleRepository.getTitle(titleId);
        List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(titleId);
        List<Map<String,Object>> wordList = new ArrayList<>();
        for(Voca row : vocaList){
            Map<String,Object> vocaListMap = new LinkedHashMap<>();
            vocaListMap.put("id",row.getId());
            vocaListMap.put("eng",row.getEng());
            vocaListMap.put("kor",row.getKor());

            wordList.add(vocaListMap);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("titleId",titleId);
        result.put("title",title);
        result.put("word",wordList);

        return result;
    }

    @Override
    public void updateVoca(String email,Long titleId, VocaUpdateDTO vocaUpdateDTO) {
        //영속성 컨텍스트로 관리되는 Title 객체
        Title title = titleRepository.findById(titleId)
                .orElseThrow(() -> new RuntimeException("Title not found"));

        List<Voca> modifiedList = vocaUpdateDTO.getModifiedWord();
        List<Long> deleteList = vocaUpdateDTO.getDeleteId();
        List<Voca> addList = vocaUpdateDTO.getAddWord();

        //title 수정
        if(vocaUpdateDTO.getModifiedTitle() != null &&
                !vocaUpdateDTO.getModifiedTitle().equals(title.getTitle())){
            title.setTitle(vocaUpdateDTO.getModifiedTitle());
            titleRepository.save(title);
            log.info("Title Modified...");
        }

        //Section이 있다면 null로 설정
        List<Long> sectionList = vocaRepository.getSectionList(title.getId());

        if ((!modifiedList.isEmpty() || !deleteList.isEmpty() || !addList.isEmpty())
                &&!sectionList.contains(null)){

            //List<Voca> 조회 및 Section reset삭제
            List<Voca> vocaList = vocaRepository.findByTitleIdOrderById(title.getId());
            for(Voca row:vocaList){
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
            for(Voca voca : modifiedList){
                voca.setUserId(email);
                voca.setTitle(title);
            }
            vocaRepository.saveAll(modifiedList);
            log.info("Voca Modified...");
        }

        //삭제
        if(!deleteList.isEmpty()){
            vocaRepository.deleteAllById(deleteList);
            log.info("Voca Delete...");

        }

        //추가
        if(!addList.isEmpty()){
            for(Voca voca: addList){
                voca.setUserId(email);
                voca.setTitle(title);
            }
            vocaRepository.saveAll(addList);
            log.info("Voca Add...");

        }
    }

    @Override
    public void deleteVoca(Long title) {
        titleRepository.deleteById(title);
    }
}
