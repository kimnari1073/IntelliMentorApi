package org.intelli.intellimentor.service;


import org.intelli.intellimentor.dto.QuizRequestDTO;

import java.util.List;
import java.util.Map;

public interface LearnService {

    void setSection(Long titleId, int section);//섹션설정
    void deleteLearn(Long titleId);//학습초기화
    void modifiyBookmark(Long titleId, List<Long>trueIdList, List<Long>falseIdList);
    Map<String, Object> getLearn(Long titleId);
    Map<String, Object> getQuiz(Long sectionId,String subject);
    Map<String,Object> markQuiz(Long sectionId, QuizRequestDTO quizRequestDTO);
}
