package org.intelli.intellimentor.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizRequestDTO {
    private List<QuizItemDTO> data;

}
