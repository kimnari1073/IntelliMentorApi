package org.intelli.intellimentor.service;


import java.util.Map;

public interface LearnService {

    void createSection(String email, String title, int section);//섹션설정
    Map<String, Object> readLearn(String email, String title);
    void deleteLearn(String email,String title);
}
