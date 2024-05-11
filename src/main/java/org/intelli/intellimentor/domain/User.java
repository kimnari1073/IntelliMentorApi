package org.intelli.intellimentor.domain;


import jakarta.persistence.*;
import lombok.*;

import javax.annotation.processing.Generated;

@Entity
@Table
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String birth;
    private String gender;
    private String role;
}

