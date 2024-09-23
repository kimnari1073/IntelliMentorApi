package org.intelli.intellimentor.dto.Voca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class VocaAllDTO {
    private Long titleId;
    private String title;
    private List<VocaSectionDTO> vocaSectionDTOs;
}
