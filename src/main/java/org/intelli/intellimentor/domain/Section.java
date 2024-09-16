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
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int section;
    private int vocaCount;
    private String grade;
    private String engScore;
    private String korScore;
    private String sentenceScore;
    @PrePersist
    public void prePersist() {
        if (this.grade == null || this.grade.isEmpty()) {
            this.grade = "-";
        }
    }
}
