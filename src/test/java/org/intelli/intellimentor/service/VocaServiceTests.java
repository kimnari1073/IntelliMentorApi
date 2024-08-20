package org.intelli.intellimentor.service;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.VocaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class VocaServiceTests {
    @Autowired
    private VocaService vocaService;

    @Test
    public void testReadDetailsVoca(){
//        VocaDTO vocaDTO=new VocaDTO();
//        vocaDTO.setUserId("user1@aaa.com");
//        vocaDTO.setTitle("테스트제목1");
//        log.info(vocaService.readDetailsVoca(vocaDTO));
    }
}
