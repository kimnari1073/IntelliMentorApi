package org.intelli.intellimentor.service;


import java.util.Map;

public interface LearnService {

    void setSection(Long titleId, int section);//섹션설정
    void deleteLearn(Long titleId);//학습초기화
    Map<String, Object> getLearn(Long titleId);
//    Map<String, Object> getQuizEng(String email, String title, int section);
//    Map<String, Object> getQuizKor(String email, String title, int section);
}
