package org.intelli.intellimentor.repository;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Log4j2
public class VocaRepositoryTests {
    @Autowired
    private VocaRepository vocaRepository;

    @Test
    public void testInsertVoca(){
        List<Voca> vocaList1 = new ArrayList<>();
        List<Voca> vocaList2 = new ArrayList<>();
        for(int i=0; i<5; i++){
            Voca voca = Voca.builder()
                    .eng("engTest"+i)
                    .kor("한글테스트"+i)
                    .title("테스트제목1")
                    .userId("user1@aaa.com")
                    .build();
            vocaList1.add(voca);
        }
        for(int i=0;i<3;i++){
            Voca voca = Voca.builder()
                    .eng("engTest"+(i+10))
                    .kor("한글테스트"+(i+10))
                    .title("테스트제목2")
                    .userId("user1@aaa.com")
                    .build();
            vocaList2.add(voca);
        }
        vocaRepository.saveAll(vocaList1);
        vocaRepository.saveAll(vocaList2);

    }
//    @Test
//    public void testReadVoca(){
//        String userId = "user1@aaa.com";
//        List<Object[]> result = vocaRepository.getVocaCount(userId);
//
//        List<VocaListDTO> result2 = result.stream()
//                .map(r -> new VocaListDTO((String) r[0],(Long) r[1]))
//                .collect(Collectors.toList());
//
//        log.info("testReadVoca------------------------");
//        log.info(result2);
//
//    }
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
    @Test
    public void testDeleteAll(){
        vocaRepository.deleteAll();
    }
    @Test
    public void testSetSection(){
        String userId="user1@aaa.com";
        String title="테스트제목1";
        int section = 2;

        List<Voca> result = vocaRepository.findByUserIdAndTitle(userId,title);
        int resultSize = result.size();

        log.info("resultSize: "+resultSize);
        log.info("섹션 설정 테스트: "+resultSize/section);

        int i=1;
        for(Voca voca:result){
            voca.setSection(i);
            i++;
            if(section<i){
                i=1;
            }
        }
        vocaRepository.saveAll(result);
        //

    }
}
