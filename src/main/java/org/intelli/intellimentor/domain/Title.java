package org.intelli.intellimentor.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table
@Getter
@Setter
@ToString(exclude = "vocas")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Title {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @OneToMany(mappedBy = "title", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Voca> vocas;
}
