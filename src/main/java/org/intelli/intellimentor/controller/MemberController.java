package org.intelli.intellimentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.dto.MemberDTO;
import org.intelli.intellimentor.dto.MemberSubDTO;
import org.intelli.intellimentor.service.MemberService;
import org.intelli.intellimentor.util.CustomJWTException;
import org.intelli.intellimentor.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    //소셜 회원가입&로그인 - 카카오
    @GetMapping("/kakao")
    public Map<String, Object> getMemberFromKakao(String accessToken) {
        MemberDTO memberDTO = memberService.getKakaoMember(accessToken);
        Map<String, Object> claims = memberDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }

    //로컬 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberSubDTO memberSubDTO) {
        memberService.register(memberSubDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 회원정보 수정
    @PutMapping("/modify")
    public ResponseEntity<?> modify(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody MemberSubDTO memberSubDTO) {
        String email = JWTUtil.JWTtoEmail(authHeader);

        memberSubDTO.setEmail(email);
        memberService.modifyMember(memberSubDTO);

        return ResponseEntity.noContent().build();
    }

    // 회원 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader) {
        String email = JWTUtil.JWTtoEmail(authHeader);

        memberService.deleteMember(email);

        return ResponseEntity.noContent().build();
    }


    //리프레쉬 토큰
    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "X-Refresh-Token")
    @PostMapping("/refresh")
    public Map<String, Object> refresh(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        if (refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH");
        }
        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID STRING");
        }

        String accessToken = authHeader.substring(7);

        if (!checkExpiredToken(accessToken)) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        log.info("refresh ... claims: " + claims);

        String newAccessToken = JWTUtil.generateToken(claims, 10);
        String newRefreshToken = checkTime((Integer) claims.get("exp")) ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }


    //RefreshToken의 시간이 1시간 미만으로 남았다면
    private boolean checkTime(Integer exp) {
        //JWT exp를 날짜로 변환
        Date expDate = new Date((long) exp * (1000));

        //현재 시간과의 차이 계산
        long gap = expDate.getTime() - System.currentTimeMillis();

        //분단위 계산
        long leftMin = gap / (1000 * 60);

        return leftMin < 60;
    }

    private boolean checkExpiredToken(String token) {
        try {
            JWTUtil.validateToken(token);
        } catch (CustomJWTException ex) {
            if (ex.getMessage().equals("Expired")) {
                return true;
            }
        }

        return false;
    }
}