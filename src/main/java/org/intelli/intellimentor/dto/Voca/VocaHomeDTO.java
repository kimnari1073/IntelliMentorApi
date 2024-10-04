package org.intelli.intellimentor.dto.Voca;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.intelli.intellimentor.domain.Voca;

@Data
@EqualsAndHashCode(callSuper = true) // 상속된 클래스에서도 equals/hashCode 메서드를 사용할 수 있게 함
@ToString(callSuper = true) // 부모 클래스 필드를 포함한 toString() 생성
public class VocaHomeDTO extends VocaItemDTO {
    private Long sectionId;

    // Voca 객체를 VocaHomeDTO로 변환하는 메서드
    public static VocaHomeDTO from(Voca voca, Long sectionId) {
        VocaHomeDTO vocaHomeDTO = new VocaHomeDTO();
        vocaHomeDTO.setId(voca.getId());
        vocaHomeDTO.setEng(voca.getEng());
        vocaHomeDTO.setKor(voca.getKor());
        vocaHomeDTO.setBookmark(voca.isBookmark());
        vocaHomeDTO.setMistakes(voca.getMistakes());
        vocaHomeDTO.setSentenceEng(voca.getSentenceEng());
        vocaHomeDTO.setSentenceKor(voca.getSentenceKor());
        vocaHomeDTO.setSectionId(sectionId); // 추가된 sectionId 설정
        return vocaHomeDTO;
    }
}
