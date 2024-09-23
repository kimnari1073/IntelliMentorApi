package org.intelli.intellimentor.dto.Voca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.intelli.intellimentor.dto.Voca.VocaItemDTO;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VocaSectionDTO {

    private Long sectionId;
    private int section;
    private int progress;
    private String grade;
    private List<VocaItemDTO> vocaItemDTOS;

}
