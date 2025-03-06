package com.paymilli.paymilli.domain.member.service;

import com.paymilli.paymilli.domain.member.domain.Member;
import com.paymilli.paymilli.domain.member.domain.MemberProfile;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.member.infrastructure.JPAMemberRepository;
import com.paymilli.paymilli.domain.member.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        Member member = memberRepository.findByLoginId(loginId)
            .orElseThrow(() -> new UsernameNotFoundException("유저정보가 없습니다."));

        MemberProfile memberProfile = member.getMemberProfile();

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(memberProfile.getRole().getValue());

        return User.builder()
            .username(member.getLoginId())
            .password(member.getPassword())
            .authorities(grantedAuthority)
            .build();
    }
}