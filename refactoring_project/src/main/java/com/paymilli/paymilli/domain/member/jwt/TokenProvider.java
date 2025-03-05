package com.paymilli.paymilli.domain.member.jwt;

import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.member.infrastructure.JPAMemberRepository;
import com.paymilli.paymilli.global.exception.BaseException;
import com.paymilli.paymilli.global.exception.BaseResponseStatus;
import com.paymilli.paymilli.global.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final RedisUtil redisUtil;
    private final JPAMemberRepository JPAMemberRepository;
    //    private final RedisTemplate<String, String> redisTemplate;
    private Key key;

    public TokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
        RedisTemplate<String, String> redisTemplate, RedisUtil redisUtil,
        JPAMemberRepository JPAMemberRepository) {
        this.secret = secret;
        this.accessTokenValidityInMilliseconds = 86400 * 1000;
//        this.accessTokenValidityInMilliseconds = 10;
        this.refreshTokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.redisUtil = redisUtil;
//        this.refreshTokenValidityInMilliseconds = 10;
        this.JPAMemberRepository = JPAMemberRepository;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createRefreshToken(Authentication authentication) {
        // 권한들
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        // 만료시간 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        UUID id = getUUID(authentication);

        String refreshToken = Jwts.builder()
            .setSubject(id.toString())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();

        redisUtil.saveDataToRedis(id.toString(), refreshToken,
            refreshTokenValidityInMilliseconds);

        return refreshToken;
    }

    public String createAccessToken(Authentication authentication) {
        // 권한들
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        // 만료시간 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        UUID id = getUUID(authentication);

        return Jwts.builder()
            .setSubject(id.toString())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }

    public String createAccessToken(Authentication authentication, String refreshToken) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        UUID id = getId(refreshToken);

        return Jwts.builder()
            .setSubject(id.toString())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }

    private UUID getUUID(Authentication authentication) {
        String loginID = authentication.getName();
        MemberEntity memberEntity = JPAMemberRepository.findByLoginId(loginID).orElseThrow();
        return memberEntity.getId();
    }

    public Authentication getAuthentication(String token) {

        // 토큰을 이용해서 Claims 만듬
        Claims claims = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        // Claims 에 들어있는 권한들을 가져옴
        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.info("authorities: {}", authorities);

        System.out.println("subject: " + claims.getSubject());
        System.out.println("issuer: " + claims.getIssuer());
        System.out.println("audience: " + claims.getAudience());
        System.out.println("expiration: " + claims.getExpiration());
        System.out.println("id: " + claims.getId());

        // 권한 정보를 이용해서 User 객체를 만듬
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw new BaseException(BaseResponseStatus.UNAUTHORIZED);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new BaseException(BaseResponseStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new BaseException(BaseResponseStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new BaseException(BaseResponseStatus.UNAUTHORIZED);
        }
    }

    public UUID getId(String token) {
        return UUID.fromString(Jwts.parserBuilder()
            .setSigningKey(key) // 동일한 키 사용
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject()); // 토큰의 subject에서 memberId 추출
    }

    public String extractAccessToken(String token) {
        return token.split(" ")[1];
    }


    public String getRefreshToken(String refreshToken) {
        return (String) redisUtil.getDataFromRedis(refreshToken);
    }

    public void removeRefreshToken(UUID memberId) {
        redisUtil.removeDataFromRedis(memberId.toString());
    }
}