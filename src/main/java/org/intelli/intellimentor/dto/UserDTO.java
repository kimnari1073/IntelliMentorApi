package org.intelli.intellimentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String birth;
    private String gender;
    private String role;
}
