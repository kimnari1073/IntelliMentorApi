package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.MemberDTO;
import org.intelli.intellimentor.dto.MemberModifyDTO;
import org.intelli.intellimentor.dto.MemberSignupDTO;
import org.intelli.intellimentor.service.MemberService;
import org.intelli.intellimentor.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    //소셜 회원가입 - 카카오
    @GetMapping("/api/member/kakao")
    public Map<String,Object> getMemberFromKakao(String accessToken){
        log.info("accessToken: "+accessToken);
        MemberDTO memberDTO = memberService.getKakaoMember(accessToken);
        Map<String,Object> claims = memberDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims,10);
        String jwtRefreshToken = JWTUtil.generateToken(claims,60*24);

        claims.put("accessToken",jwtAccessToken);
        claims.put("refreshToken",jwtRefreshToken);

        return claims;
    }

    //로컬 회원가입
    @PostMapping("/api/member/signup")
    public ResponseEntity<Map<String,String>> signup(@RequestBody MemberSignupDTO memberSignupDTO){
        try{
            memberService.register(memberSignupDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("email",memberSignupDTO.getEmail()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("ERROR_MESSAGE",e.getMessage()));
        }

    }

    //회원정보 수정
    @PutMapping("/api/member/modify")
    public Map<String, String> modify(@RequestBody MemberModifyDTO memberModifyDTO){
        memberService.modifyMember(memberModifyDTO);
        return Map.of("result","modified");
    }
}
