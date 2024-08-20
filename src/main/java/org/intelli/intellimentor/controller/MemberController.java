package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.MemberDTO;
import org.intelli.intellimentor.dto.MemberSubDTO;
import org.intelli.intellimentor.service.MemberService;
import org.intelli.intellimentor.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    //소셜 회원가입&로그인 - 카카오
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
    public ResponseEntity<Map<String,String>> signup(@RequestBody MemberSubDTO memberSubDTO){
        memberService.register(memberSubDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("email",memberSubDTO.getEmail()));
    }

    // 회원정보 수정
    @PutMapping("/modify")
    public ResponseEntity<?> modify(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody MemberSubDTO memberSubDTO) {
        log.info("MemberSubDTO: " + memberSubDTO);

        String token = authHeader.substring(7);
        Map<String, Object> claims = JWTUtil.validateToken(token);

        memberSubDTO.setEmail((String) claims.get("email"));
        memberService.modifyMember(memberSubDTO);

        return ResponseEntity.noContent().build();
    }

    // 회원 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Map<String, Object> claims = JWTUtil.validateToken(token);

        memberService.deleteMember((String) claims.get("email"));

        return ResponseEntity.noContent().build();
    }
}
