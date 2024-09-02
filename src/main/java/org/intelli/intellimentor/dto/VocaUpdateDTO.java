package org.intelli.intellimentor.dto;

import lombok.Data;
import org.intelli.intellimentor.domain.Voca;

import java.util.List;

@Data
public class VocaUpdateDTO {
    //원본
    private Long titleId;
    //수정할 제목
    private String modifiedTitle;

    private List<Voca> modifiedWord;
    private List<Long> deleteId;
    private List<Voca> addWord;

}
