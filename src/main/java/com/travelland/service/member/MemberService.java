package com.travelland.service.member;

import com.travelland.domain.member.Member;
import com.travelland.domain.member.RefreshToken;
import com.travelland.dto.member.MemberDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.security.JwtUtil;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.member.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public boolean changeNickname(MemberDto.ChangeNicknameRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.changeNickname(request.getNickname());
        return true;
    }

    @Transactional(readOnly = true)
    public boolean checkNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();
    }

    @Transactional
    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtUtil.getJwtFromHeader(request);
        if (token == null) return false;

        RefreshToken tokenInfo = refreshTokenRepository.findByAccessToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_TOKEN));

        refreshTokenRepository.delete(tokenInfo);

        SecurityContextHolder.clearContext();

        return true;
    }

    @Transactional
    public boolean signout(HttpServletRequest request, HttpServletResponse response, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);

        logout(request, response);

        return true;
    }

    @Transactional
    public MemberDto.MemberInfo getMemberInfo() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();

        return new MemberDto.MemberInfo(member);
    }
}
