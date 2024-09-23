package org.intelli.intellimentor.dto.Voca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VocaItemDTO {
    private Long id;
    private String eng;
    private String kor;
    private boolean bookmark;
    private int mistakes;
    private String sentenceEng;
    private String sentenceKor;

}
