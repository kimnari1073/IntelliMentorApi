package org.intelli.intellimentor.dto;

import lombok.Data;

@Data
public class QuizItemDTO {
    private Long id;
    private String type;
    private Boolean correct;
}
