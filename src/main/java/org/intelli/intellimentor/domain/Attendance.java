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
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private Boolean Mon;
    private Boolean Tue;
    private Boolean Wen;
    private Boolean Thu;
    private Boolean Fri;
    private Boolean Sat;
    private Boolean Sun;
}
