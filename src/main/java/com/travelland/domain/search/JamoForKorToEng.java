package com.travelland.domain.search;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class JamoForKorToEng {
    private final String[] chosung;
    private final String[] jungsung;
    private final String[] jongsun;
    private final Map<String, String> doubleLetter;
    private final Map<String,String> kor2Eng;
    private JamoForKorToEng(){
        this.chosung = new String[]{"ㄱ", "ㄱ", "ㄴ", "ㄷ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅂ", "ㅅ", "ㅅ", "ㅇ", "ㅈ",
                "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};

        this.jungsung = new String[]{"ㅏ", "ㅐ", "ㅑ", "ㅐ", "ㅓ", "ㅔ", "ㅕ", "ㅔ", "ㅗ", "ㅗㅏ",
                "ㅗㅐ", "ㅗㅣ", "ㅛ", "ㅜ", "ㅜㅓ", "ㅜㅔ", "ㅜㅣ", "ㅠ", "ㅡ", "ㅡㅣ", "ㅣ"};

        this.jongsun = new String[]{"", "ㄱ", "ㄱ", "ㄱㅅ", "ㄴ", "ㄴㅈ", "ㄴㅎ", "ㄷ",
                "ㄹ", "ㄹㄱ", "ㄹㅁ", "ㄹㅂ", "ㄹㅅ", "ㄹㅌ", "ㄹㅍ", "ㄹㅎ", "ㅁ", "ㅂ", "ㅂㅅ", "ㅅ", "ㅅ", "ㅇ",
                "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};

        this.doubleLetter = this.doubleLetter("ㅃ,ㅂ/ㅉ,ㅈ/ㄸ,ㄷ/ㄲ,ㄱ/ㅆ,ㅅ/ㅒ,ㅐ/ㅖ,ㅔ/" +
                "ㅘ,ㅗㅏ/ㅚ,ㅗㅣ/ㅙ,ㅗㅐ/ㅝ,ㅜㅓ/ㅞ,ㅜㅔ/ㅟ,ㅜㅣ/ㅢ,ㅡㅣ");

        this.kor2Eng = this.kor2EngMapping("ㅂ,q/ㅈ,w/ㄷ,e/ㄱ,r/ㅅ,t/ㅛ,y/ㅕ,u/ㅑ,i/ㅐ,o/ㅔ,p/ㅁ,a/" +
                "ㄴ,s/ㅇ,d/ㄹ,f/ㅎ,g/ㅗ,h/ㅓ,j/ㅏ,k/ㅣ,l/ㅋ,z/ㅌ,x/ㅊ,c/ㅍ,v/ㅠ,b/ㅜ,n/ㅡ,m");
    }

    public String korToEng(String target){
        StringBuilder sb = new StringBuilder();
        Arrays.stream(this.splitAndJoin(target).split("")).map(kor2Eng::get).forEach(sb::append);
        return sb.toString();
    }

    public String splitAndJoin(String target) {
        String[] strArray = target.split("");
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            this.separateKey(str, sb);
        }
        return sb.toString();
    }

    private void separateKey(String target, StringBuilder sb) {
        int codePoint = Character.codePointAt(target, 0);

        if(codePoint < 0xAC00 || codePoint > 0xD79D){
            sb.append(separateCompoundAlphabet(target));
            return;
        }
        int startVal = codePoint - 0xAC00;
        int jong = startVal % 28;
        int jung = ((startVal - jong) / 28) % 21;
        int cho = (((startVal - jong) / 28) - jung) / 21;

        sb.append(chosung[cho]);
        sb.append(jungsung[jung]);
        sb.append(jongsun[jong]);
    }
    private String separateCompoundAlphabet(String alphabet){
        return doubleLetter.getOrDefault(alphabet,alphabet);
    }
    private Map<String,String> kor2EngMapping(String keyMap){
        Map<String,String> result = new HashMap<>();
        for(String key : keyMap.split("/")){
            String[] strs = key.split(",");
            result.put(strs[0],strs[1]);
        }
        return result;
    }

    private Map<String, String> doubleLetter(String keyMap){
        Map<String,String> result = new HashMap<>();
        for(String dict : keyMap.split("/")){
            String[] strs = dict.split(",");
            result.put(strs[0],strs[1]);
        }
        return  result;
    }
}