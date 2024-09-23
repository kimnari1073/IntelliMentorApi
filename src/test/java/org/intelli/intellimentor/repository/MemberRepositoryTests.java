package org.intelli.intellimentor.repository;

import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.domain.Member;
import org.intelli.intellimentor.domain.MemberRole;
import org.intelli.intellimentor.domain.Voca;
import org.intelli.intellimentor.dto.Voca.VocaItemDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
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
    public void testGetHome(){
        //초기 데이터 설정
        String email = "user1@aaa.com";

        //로직
        List<Voca> vocaList = vocaRepository.findByUserIdAndSectionIdIsNotNullAndSentenceEngIsNotNull(email);
        List<Voca> topVocaList = vocaList.stream()
                .sorted(Comparator.comparingInt(Voca::getMistakes).reversed()) // mistakes 필드를 기준으로 내림차순 정렬
                .limit(5) // 상위 5개만 선택
                .toList(); // 리스트로 변환

        Random random = new Random();
        Voca voca = topVocaList.get(random.nextInt(topVocaList.size()));
        VocaItemDTO vocaItemDTO = VocaItemDTO.fromEntity(voca);

        log.info(vocaItemDTO);

    }
}
