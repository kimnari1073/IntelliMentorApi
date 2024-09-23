package org.intelli.intellimentor.dto;

import lombok.Data;

@Data
public class VocaItemDTO {
    private Long id;
    private String eng;
    private String kor;
    private boolean bookmark;
    private int mistakes;
    private String sentenceEng;
    private String sentenceKor;

}
