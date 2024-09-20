package org.intelli.intellimentor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

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

    @Column(nullable = false)
    private String userId;
    private String eng;
    private String kor;
    private boolean bookmark;
    private int mistakes;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String sentenceEng;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String sentenceKor;

    @ManyToOne
    @JoinColumn(name = "title_id", nullable = false)
    @JsonIgnore
    private Title title;

    @ManyToOne
    @JoinColumn(name = "section_id")
    @JsonIgnore
    private Section section;

    @PrePersist
    public void prePersist() {
        this.bookmark = false;
        this.mistakes = 0;
    }
}
