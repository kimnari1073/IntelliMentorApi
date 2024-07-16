package org.intelli.intellimentor.service;

import org.intelli.intellimentor.dto.MemberDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberService {
    MemberDTO getKakaoMember(String accessToken);

}
