package org.intelli.intellimentor.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Voca {

    @EmbeddedId
    private VocaId vocaId;

    private String kor;

    @MapsId("vocaListId") // EmbeddedId 내 vocaListId 필드를 매핑
    @ManyToOne
    @JoinColumn(name="voca_list_id", referencedColumnName = "voca_list_id")
    private VocaList vocaList; // 여기에서만 외래키 관리
}
