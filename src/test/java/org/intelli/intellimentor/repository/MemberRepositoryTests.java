package org.intelli.intellimentor.repository;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Member;
import org.intelli.intellimentor.domain.MemberRole;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.Voca.VocaHomeDTO;
import org.intelli.intellimentor.dto.Voca.VocaItemDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SpringBootTest
@Log4j2
public class MemberRepositoryTests {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VocaRepository vocaRepository;

    @Test
    public void testInsertMember(){
        for (int i = 0; i < 10; i++) {
            Member member = Member.builder()
                    .email("user"+i+"@aaa.com")
                    .pw(passwordEncoder.encode("1111"))
                    .nickname("USER"+i)
                    .build();

            member.addRole(MemberRole.USER);
            if(i>=5){
                member.addRole(MemberRole.MANAGER);
            }
            if(i>=8){
                member.addRole(MemberRole.ADMIN);
            }
            memberRepository.save(member);
        }
    }

    @Test
    public void testRead(){
        String email = "user9@aaa.com";

        Member member = memberRepository.getWithRoles(email);
        log.info("----------------------");
        log.info(member);
        log.info(member.getMemberRoleList());
    }

    @Test
    public void testDeleteAll(){
        memberRepository.deleteAll();
    }


}
