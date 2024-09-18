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

    @Column(nullable = false)
    private String userId;
    private String eng;
    private String kor;
    private boolean bookmark;
    private int mistakes;
    private String sentence;

    @ManyToOne
    @JoinColumn(name = "title_id", nullable = false)
    private Title title;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @PrePersist
    public void prePersist() {
        this.bookmark = false;
        this.mistakes = 0;
    }
}
