package org.intelli.intellimentor.dto;

import lombok.Data;

@Data
public class VocaListDTO {
    private String title;
    private Long vocaCount;
    private int section;

    public VocaListDTO(String title, Long vocaCount, int section) {
        this.title = title;
        this.vocaCount = vocaCount;
        this.section = section;
    }
}
