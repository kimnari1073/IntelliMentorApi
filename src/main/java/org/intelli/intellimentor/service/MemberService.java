package org.intelli.intellimentor.service;

import org.intelli.intellimentor.domain.Member;
import org.intelli.intellimentor.dto.MemberDTO;
import org.intelli.intellimentor.dto.MemberSubDTO;
import org.intelli.intellimentor.dto.Voca.VocaItemDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

public interface MemberService {

    MemberDTO getKakaoMember(String accessToken);
    void register(MemberSubDTO memberSubDTO);
    void modifyMember(MemberSubDTO memberSubDTO);
    void deleteMember(String email);
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
