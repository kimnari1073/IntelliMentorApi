package org.intelli.intellimentor.dto.Voca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.intelli.intellimentor.domain.Voca;

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
    // Voca 객체를 VocaItemDTO로 변환하는 메서드
    public static VocaItemDTO fromEntity(Voca voca) {
        VocaItemDTO vocaItemDTO = new VocaItemDTO();
        vocaItemDTO.setId(voca.getId());
        vocaItemDTO.setEng(voca.getEng());
        vocaItemDTO.setKor(voca.getKor());
        vocaItemDTO.setBookmark(voca.isBookmark());
        vocaItemDTO.setMistakes(voca.getMistakes());
        vocaItemDTO.setSentenceEng(voca.getSentenceEng());
        vocaItemDTO.setSentenceKor(voca.getSentenceKor());
        return vocaItemDTO;
    }

}
