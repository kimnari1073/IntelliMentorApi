package org.intelli.intellimentor.dto;


import lombok.Data;

@Data
public class MemberSingupDTO {
    private String email;
    private String pw;
    private String pwCheck;
    private String nickname;
}
