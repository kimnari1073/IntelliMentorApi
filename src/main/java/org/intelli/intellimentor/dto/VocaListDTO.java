package org.intelli.intellimentor.dto;

import lombok.Data;

@Data
public class VocaListDTO {
    private String title;
    private Long vocaCount;

    public VocaListDTO(String title, Long vocaCount) {
        this.title = title;
        this.vocaCount = vocaCount;
    }
}
