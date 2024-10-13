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
    private String userId;
    private Boolean Mon;
    private Boolean Tue;
    private Boolean Wed;
    private Boolean Thu;
    private Boolean Fri;
    private Boolean Sat;
    private Boolean Sun;
}
