package org.intelli.intellimentor.dto.Voca;

import lombok.Data;

import java.util.List;

@Data
public class VocaDTO {
    private String title;
    private List<String> eng;
    private List<String> kor;

}
