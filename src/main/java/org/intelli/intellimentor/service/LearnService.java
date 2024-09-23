package org.intelli.intellimentor.service;


import org.intelli.intellimentor.dto.QuizRequestDTO;
import org.intelli.intellimentor.dto.Voca.VocaAllDTO;
import org.intelli.intellimentor.dto.Voca.VocaSectionDTO;

import java.util.Map;

public interface LearnService {

    void setSection(Long titleId, int section);//섹션설정
    void deleteLearn(Long titleId);//학습초기화
    void setBookmark(Long vocaId);
    VocaAllDTO getLearn(Long titleId);//학습 조회(전체)
    VocaSectionDTO getLearnBySection(Long sectionId);
    Map<String, Object> getQuiz(Long sectionId,String subject);
    Map<String,Object> markQuiz(Long sectionId, QuizRequestDTO quizRequestDTO);
}
