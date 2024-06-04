package org.intelli.intellimentor.data;

import org.intelli.intellimentor.domain.VocaList;
import org.intelli.intellimentor.repository.UserRepository;
import org.intelli.intellimentor.repository.VocaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataInsertTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VocaRepository vocaRepository;


    @Test
    void insertVoca(){

        VocaList vocaList = new VocaList();
        vocaList.setTitle("단어장테스트");

        vocaList.setUser(userRepository.findById(2L).orElseThrow());



        vocaRepository.save(vocaList);


    }
}
