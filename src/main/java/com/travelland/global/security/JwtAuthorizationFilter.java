package com.travelland.global.security;

import com.travelland.domain.Member;
import com.travelland.domain.RefreshToken;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.jwt.JwtUtil;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.member.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = jwtUtil.getJwtFromCookie(request);

        if (!StringUtils.hasText(tokenValue)) {
            filterChain.doFilter(request, response);
            return;
        }

        // access token 유효하지 않으면 refresh token 유효성 검사
        if (!jwtUtil.validateToken(tokenValue)) {
            RefreshToken tokenInfo = refreshTokenRepository.findByAccessToken(tokenValue)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_TOKEN));

            String refreshToken = tokenInfo.getRefreshToken();
            if (!jwtUtil.validateToken(refreshToken)) {
                refreshTokenRepository.delete(tokenInfo);
                return;
            }

            // refresh token 유효하면 access token 새로 발급
            Member member = memberRepository.findById(tokenInfo.getMemberId())
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            tokenValue = jwtUtil.createToken(member.getEmail(), member.getRole());
            refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken, tokenValue));

            jwtUtil.addJwtToCookie(tokenValue, response);
        }

        Claims claims = jwtUtil.getUserInfoFromToken(tokenValue);

        try {
            setAuthentication(claims.getSubject());
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
