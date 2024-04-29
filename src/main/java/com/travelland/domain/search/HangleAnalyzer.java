package com.travelland.domain.search;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 한글 문장에서 형태소를 분석하는 클래스
 *
 * @author     kjw
 * @version    1.0.0
 * @since      1.0.0
 */
@Component
public class HangleAnalyzer {
    /**
     * 기본 한글 형태소 분석 클래스
     */
    private final Komoran komoran;
    /**
     * 형태소 분석을 LIGHT 모델에 위키피디아의 타이틀을 NNP(고유명사)로 포함해서 학습한 MODEL로 설정
     */
    public HangleAnalyzer(){
        this.komoran = new Komoran(DEFAULT_MODEL.FULL);
    }

    /**
     * 한글 문장을 입력 받아 형태소 분석을 실행하고 명사, 대명사, 형용사에 해당하는 단어들만 반환
     * @param string 분석 대상 한글 문자열
     * @return 명사, 대명사, 형용사 만 Set 형태로 반환
     */
    public Set<String> analyzeHangle(String string){
        KomoranResult analyzeResultList = komoran.analyze(string);
        List<Token> tokenList = analyzeResultList.getTokenList();
        Set<String> result = new HashSet<>();

        for (Token token : tokenList) {
            if(token.getPos().matches("^(NP|NNP|NNG|VA|VV)$"))
                result.add(token.getMorph());
        }
        return result;
    }
}
