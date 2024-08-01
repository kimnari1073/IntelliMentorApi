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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voca_list_id", nullable = false)
    private VocaList vocaList;

    private String eng;
    private String kor;
}
