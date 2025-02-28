package com.paymilli.paymilli.domain.member.service;

import com.paymilli.paymilli.domain.member.entity.Member;
import com.paymilli.paymilli.domain.member.repository.MemberRepository;
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

        Member member = memberRepository.findByMemberId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("유저정보가 없습니다."));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole().getValue());

        return User.builder()
            .username(member.getMemberId())
            .password(member.getPassword())
            .authorities(grantedAuthority)
            .build();
    }
}