package org.intelli.intellimentor.domain;

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
    private String sentence;

    @ManyToOne
    @JoinColumn(name = "title_id", nullable = false)
    @JsonBackReference // Jackson에게 역참조 방지를 알림
    private Title title;

    @ManyToOne
    @JoinColumn(name = "section_id")
    @JsonBackReference // Jackson에게 역참조 방지를 알림
    private Section section;

    @PrePersist
    public void prePersist() {
        this.bookmark = false;
        this.mistakes = 0;
    }
}
