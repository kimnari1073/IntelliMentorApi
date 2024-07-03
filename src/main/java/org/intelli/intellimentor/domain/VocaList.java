package org.intelli.intellimentor.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class VocaList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="voca_list_id")
    private Long vocaListId;
    private String title;

    @ManyToOne
    @JoinColumn(name = "email")
    private Member member;  // Member를 참조하는 외래키
}
