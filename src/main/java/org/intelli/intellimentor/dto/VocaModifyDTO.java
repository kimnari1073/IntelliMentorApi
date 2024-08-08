package org.intelli.intellimentor.dto;

import lombok.Data;

import java.util.List;

@Data
public class VocaModifyDTO {
    private String userId;
    private String title;
    private String modifiedTitle;
    private List<String> modifiedEng;
    private List<String> modifiedKor;
}
