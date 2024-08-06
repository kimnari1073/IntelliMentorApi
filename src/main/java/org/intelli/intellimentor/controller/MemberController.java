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
import java.util.NoSuchElementException;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    //소셜 회원가입 - 카카오
    @GetMapping("/kakao")
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
    @PostMapping("/signup")
    public ResponseEntity<Map<String,String>> signup(@RequestBody MemberSignupDTO memberSignupDTO){
        try{
            memberService.register(memberSignupDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("email",memberSignupDTO.getEmail()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("ERROR_MESSAGE",e.getMessage()));
        }

    }

    //회원정보 수정
    @PutMapping("/modify")
    public ResponseEntity<Map<String,String>> modify(@RequestBody MemberModifyDTO memberModifyDTO){
        try{
            memberService.modifyMember(memberModifyDTO);
            return ResponseEntity.noContent().build();
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("ERROR_MESSAGE",e.getMessage()));
        }
    }
    //회원 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String,String>> delete(@RequestBody MemberModifyDTO memberDTO){
        try{
            memberService.deleteMember(memberDTO);
            return ResponseEntity.noContent().build();
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("ERROR_MESSAGE",e.getMessage()));
        }
    }
}
