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

    @Test
    public void testGetHome() {
        // 초기 데이터 설정
        String userId = "user1@aaa.com";

        // 로직
        List<Voca> vocaList = vocaRepository.findByUserIdAndSectionIdIsNotNullAndSentenceEngIsNotNull(userId);
        List<Voca> topVocaList = vocaList.stream()
                .filter(voca -> voca.getMistakes() > 0) // mistakes 필드가 1 이상인 경우만 필터링
                .toList(); // 리스트로 변환

        VocaHomeDTO vocaHomeDTO = null;
        if (!topVocaList.isEmpty()) {
            // ThreadLocalRandom을 사용하여 랜덤하게 1개의 단어 선택
            Voca voca = topVocaList.get(ThreadLocalRandom.current().nextInt(topVocaList.size()));
            vocaHomeDTO = VocaHomeDTO.from(voca, voca.getSection().getId());
        } else { //틀린 단어가 없으면
            Voca voca = vocaList.get(ThreadLocalRandom.current().nextInt(vocaList.size()));
            vocaHomeDTO = VocaHomeDTO.from(voca, voca.getSection().getId());

        }

        log.info(vocaHomeDTO);
    }
}
