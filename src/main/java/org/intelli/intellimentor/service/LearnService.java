package org.intelli.intellimentor.service;


import java.util.Map;

public interface LearnService {

    void createLearn(String email, String title, int section);//섹션설정
    Map<String, Object> readLearn(String email, String title);
}