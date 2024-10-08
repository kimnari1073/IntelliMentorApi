package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Member;
import org.intelli.intellimentor.domain.MemberRole;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.MemberDTO;
import org.intelli.intellimentor.dto.MemberSubDTO;
import org.intelli.intellimentor.dto.Voca.VocaHomeDTO;
import org.intelli.intellimentor.dto.Voca.VocaItemDTO;
import org.intelli.intellimentor.repository.MemberRepository;
import org.intelli.intellimentor.repository.VocaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final VocaRepository vocaRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    public MemberDTO getKakaoMember(String accessToken) {
        //accessToken을 이용해서 사용자 정보 가져오기
        //가져온 닉네임== 이메일 주소에 해당
        String nickname = getEmailFromKakaoAccessToken(accessToken);

        //기존 DB에 회원 정보가 있는 경우, 없는 경우
        Optional<Member> result = memberRepository.findById(nickname);
        if(result.isPresent()){
            MemberDTO memberDTO = entityToDTO(result.get());
            return memberDTO;
        }
        Member socialMember = makeMember(nickname);
        memberRepository.save(socialMember);
        MemberDTO memberDTO = entityToDTO(socialMember);

        return memberDTO;
    }

    //로컬 회원 가입
    @Override
    public void register(MemberSubDTO memberSubDTO) {
        if(memberRepository.existsByEmail(memberSubDTO.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }
        if(!memberSubDTO.getPw().equals(memberSubDTO.getPwCheck())){
            throw new IllegalArgumentException("Passwords do not match");
        }
        Member member=Member.builder()
                .email(memberSubDTO.getEmail())
                .pw(passwordEncoder.encode(memberSubDTO.getPw()))
                .nickname(memberSubDTO.getNickname())
                .build();
        memberRepository.save(member);

    }
    //회원 정보 수정
    @Override
    public void modifyMember(MemberSubDTO memberSubDTO) {
        log.info("memberDubDTO - service: "+memberSubDTO);
        Optional<Member> result = memberRepository.findById(memberSubDTO.getEmail());
        Member member = result.orElseThrow(() -> new NoSuchElementException("Member not found"));

        member.changeNickname(memberSubDTO.getNickname());
        member.changePw(passwordEncoder.encode(memberSubDTO.getPw()));

        memberRepository.save(member);
    }

    //회원 삭제
    @Override
    public void deleteMember(String email) {
        try {
            Optional<Member> result = memberRepository.findById(email);
            Member member = result.orElseThrow(() -> new NoSuchElementException("Member not found"));
            memberRepository.delete(member);
        } catch (NoSuchElementException e) {
            log.warn("Attempted to delete a member that was not found: " + email);
        }




    }


    private Member makeMember(String nickname){
        String tempPassword = makeTempPassword();
        log.info("tempPassword: "+tempPassword);
        Member member = Member.builder()
                .email(nickname)
                .pw(passwordEncoder.encode(tempPassword))
                .nickname("Social Member")
                .social(true).build();
        member.addRole(MemberRole.USER);

        return member;
    }

    private String getEmailFromKakaoAccessToken(String accessToken){
        String kakaoGetUserUrl = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+accessToken);
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);


        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserUrl).build();
        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toUri(), HttpMethod.GET,entity,LinkedHashMap.class);
        log.info("response-------------------------------------");
        log.info(response);
        LinkedHashMap<String,LinkedHashMap> bodyMap = response.getBody();

        LinkedHashMap<String,String> kakaoAccount = bodyMap.get("properties");
        log.info("kakaoAccount: "+kakaoAccount);

        String nickname = kakaoAccount.get("nickname");
        log.info("nickname: "+nickname);
        return nickname;
    }

    private String makeTempPassword(){
        StringBuffer buffer= new StringBuffer();
        for(int i =0;i<10;i++){
            buffer.append((char)((int)(Math.random()*55)+65));
        }
        return buffer.toString();
    }
}
