package org.intelli.intellimentor.dto.Voca;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VocaDTO {
    private String title;
    private List<String> eng;
    private List<String> kor;
}
