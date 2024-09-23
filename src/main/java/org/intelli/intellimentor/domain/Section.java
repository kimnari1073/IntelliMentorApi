package org.intelli.intellimentor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Voca> vocas;

    @PrePersist
    public void prePersist() {
        if (this.grade == null || this.grade.isEmpty()) {
            this.grade = "-";
        }
        if (this.progress == null) {
            this.progress = 0;
        }
        if (this.engScore == null){
            this.engScore = 0;
        }
        if(this.korScore == null){
            this.korScore = 0;
        }
        if(this.senScore == null){
            this.senScore = 0;
        }
    }
}
