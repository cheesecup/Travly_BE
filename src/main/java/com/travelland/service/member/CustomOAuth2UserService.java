package com.travelland.service.member;

import com.travelland.domain.member.Member;
import com.travelland.dto.member.OAuthAttributes;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> originAttributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, originAttributes);
        Member member = saveOrUpdate(attributes);

        return new UserDetailsImpl(member, oAuth2User.getAttributes());
    }

    private Member saveOrUpdate(OAuthAttributes authAttributes) {
        Member member = memberRepository.findByEmail(authAttributes.getEmail())
                .map(entity -> entity.changeProfileImage(authAttributes.getProfileImageUrl()))
                .orElseGet(authAttributes::toEntity);

        return memberRepository.save(member);
    }
}