package org.intelli.intellimentor.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String title;

    @OneToMany(mappedBy = "vocaList", cascade = CascadeType.ALL)
    private List<Voca> voca;
}
