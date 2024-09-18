package org.intelli.intellimentor.domain;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

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
    private Integer vocaCount;
    private String grade;
    private Integer engScore;
    private Integer korScore;
    private Integer senScore;
    private Integer progress;

    @OneToMany(mappedBy = "section")
    @JsonManagedReference // Jackson에서 순방향 참조를 허용
    private List<Voca> vocas;

    @PrePersist
    public void prePersist() {
        if (this.grade == null || this.grade.isEmpty()) {
            this.grade = "-";
        }
        if (this.progress == null) {
            this.progress = 0;
        }
    }
}
