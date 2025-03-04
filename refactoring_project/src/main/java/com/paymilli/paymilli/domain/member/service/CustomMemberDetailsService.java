package com.paymilli.paymilli.domain.member.service;

import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.member.infrastructure.MemberRepository;
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
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        MemberEntity memberEntity = memberRepository.findByMemberId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("유저정보가 없습니다."));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(memberEntity.getRole().getValue());

        return User.builder()
            .username(memberEntity.getMemberId())
            .password(memberEntity.getPassword())
            .authorities(grantedAuthority)
            .build();
    }
}