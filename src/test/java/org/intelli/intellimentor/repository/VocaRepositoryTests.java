package org.intelli.intellimentor.repository;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Voca;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Log4j2
public class VocaRepositoryTests {
    @Autowired
    private VocaRepository vocaRepository;

    @Test
    public void testInsertVoca(){
        List<Voca> vocaList = new ArrayList<>();
        for(int i=0; i<10; i++){
            Voca voca = Voca.builder()
                    .eng("engTest"+i)
                    .kor("한글테스트"+i)
                    .title("제목테스트")
                    .userId("user1@aaa.com")
                    .build();
            vocaList.add(voca);
        }
        vocaRepository.saveAll(vocaList);

    }
}
