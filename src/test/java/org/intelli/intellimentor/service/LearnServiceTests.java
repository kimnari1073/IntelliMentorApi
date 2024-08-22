package org.intelli.intellimentor.service;

import org.intelli.intellimentor.repository.VocaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LearnServiceTests {
    @Autowired
    private VocaRepository vocaRepository;

    @Test
    public void testCreateLearn(){

    }
}
