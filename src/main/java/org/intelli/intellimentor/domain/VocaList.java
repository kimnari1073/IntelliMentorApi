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
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;  // User를 참조하는 외래키
}
