package org.intelli.intellimentor.service;

import org.intelli.intellimentor.domain.Member;
import org.intelli.intellimentor.dto.MemberDTO;
import org.intelli.intellimentor.dto.MemberModifyDTO;
import org.intelli.intellimentor.dto.MemberSingupDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Transactional
public interface MemberService {
    MemberDTO getKakaoMember(String accessToken);
    void register(MemberSingupDTO memberSingupDTO);
    boolean checkEmailExists(String email);
    void modifyMember(MemberModifyDTO memberModifyDTO);

    default MemberDTO entityToDTO(Member member){
        MemberDTO dto = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList().stream().map(memberRole->memberRole.name()).collect(Collectors.toList()));
        return dto;
    }

}
