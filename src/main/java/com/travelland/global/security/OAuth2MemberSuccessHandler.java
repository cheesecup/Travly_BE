package com.travelland.global.security;

import com.travelland.domain.member.Member;
import com.travelland.domain.member.RefreshToken;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.member.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@Slf4j
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl oAuth2User = (UserDetailsImpl) authentication.getPrincipal();

        String email = oAuth2User.getUsername();

        redirect(request, response, email);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String email) throws IOException {
        log.info("Token 생성 시작");
        Member member = memberRepository.findByEmail(email).orElseThrow();
        String accessToken = jwtUtil.createToken(email, member.getRole());
        String refreshToken = jwtUtil.createRefreshToken();
        refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken, accessToken));
        memberRepository.save(member);

        String uri = createURI(accessToken).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private URI createURI(String accessToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("Authorization", accessToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host("www.travly.site")
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}