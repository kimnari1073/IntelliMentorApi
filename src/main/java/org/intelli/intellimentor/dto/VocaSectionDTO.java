package org.intelli.intellimentor.dto;

import lombok.Data;

import java.util.List;

@Data
public class VocaSectionDTO {

    private Long sectionId;
    private int section;
    private int progress;
    private List<VocaItemDTO> vocaItemDTOS;

}
