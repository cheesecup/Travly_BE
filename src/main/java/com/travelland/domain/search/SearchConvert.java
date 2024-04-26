package com.travelland.domain.search;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SearchConvert {
    private final Komoran komoran;

    public SearchConvert(){
        this.komoran = new Komoran(DEFAULT_MODEL.FULL);
    }


//
//    public String changeToEng(String string){
//        StringBuilder sb = new StringBuilder();
//
//        for(int i = 0 ; i < string.length() ; i++){
//            if(this.titleKorEngMap.containsKey(string.charAt(i))){
//                sb.append(this.titleKorEngMap.get(string.charAt(i)));
//                continue;
//            }
//            sb.append(string.charAt(i));
//        }
//        return sb.toString();
//    }
//    public List<String> changeToEng(List<String> strs) {
//       List<String> result = new ArrayList<>();
//        for(String str : strs){
//            this.changeToEng(str);
//       }
//    return result;
//    }

    public List<String> changeToChoseong(String string){
        KomoranResult analyzeResultList = komoran.analyze(string);
        List<Token> tokenList = analyzeResultList.getTokenList();
        List<String> result = new ArrayList<>();

        for (Token token : tokenList) {
            if(token.getPos().matches("^(NP|NNP|NNG)$"))
                result.add(token.getMorph());
        }

        if(result.isEmpty())
            result.add("@NONE");

        return result;
    }
    public List<String> changeToChoseong(List<String> strs) {
        return strs.stream()
                .flatMap(str -> changeToChoseong(str).stream())
                .toList();
    }
}
