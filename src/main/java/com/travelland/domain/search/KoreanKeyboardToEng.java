package com.travelland.domain.search;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 영어 알파벳을 한글 키보드 입력으로 작성한 경우 원래의 값으로 변환 <br><br>
 * EX> ㅑㅔㅙㅜㄷ -> iphone 으로 변환
 * @author     kjw
 * @version    1.0.0
 * @since      1.0.0
 */
@Component
public class KoreanKeyboardToEng {
    /**
     * 유니코드 계산 후 매핑하기 위한 초성값
     */
    private final String[] chosung;
    /**
     * 유니코드 계산 후 매핑하기 위한 중성값
     */
    private final String[] jungsung;
    /**
     * 유니코드 계산 후 매핑하기 위한 종성값
     */
    private final String[] jongsun;
    /**
     * 겹자음이나 겹모음에 대한 매핑
     */
    private final Map<String, String> doubleLetter;
    /**
     * 한글 키보드에 해당하는 알파벳 매핑
     */
    private final Map<String,String> kor2Eng;

    /**
     * 빈 주입시 최초 1번 매핑값 입력
     */
    private KoreanKeyboardToEng(){
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

    /**
     * 한글 한글자 한글자 분리하여 영어 알파벳으로 변환하고 영단어로 반환
     * @param target 분석 대상 한글 문자열
     * @return 원래 입력하려던 영어 keyboard 입력값
     */
    public String korToEng(String target){
        StringBuilder sb = new StringBuilder();
        Arrays.stream(this.splitAndJoin(target).split("")).map(kor2Eng::get).forEach(sb::append);
        return sb.toString();
    }
    /**
     * 유니코드를 연산하여 초성,중성,종성에 해당하는 기호로 변경<br><br>
     * EX>한글 -> ㅎㅏㄴㄱㅡㄹ
     * @param target 분석 대상 한글 문자열
     * @return 한글 기호로 분리된 문자열
     */
    public String splitAndJoin(String target) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(target.split("")).forEach(str -> this.separateKey(str,sb));
        return sb.toString();
    }
    /**
     * 들어온 한글 1 자를 초성 중성 종성으로 분리
     * @param target 분석 대상 한글 문자열
     * @param sb 결과 반환을 위한 스트링빌더
     */
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
    /**
     * 겹자음 겹모음 분리 <br><br>
     * EX> ㅙ -> ㅗㅐ
     * @param alphabet 분석 대상 한글 기호
     * @return 겹자음 또는 겹모음을 단일 모음으로 변환
     */
    private String separateCompoundAlphabet(String alphabet){
        return doubleLetter.getOrDefault(alphabet,alphabet);
    }
    /**
     * 영어/한글 기호 매핑 문자열을 Map 형태로 변환
     * @param keyMap 사전에 정의한 한글 키보드에 해당하는 영어 알파벳값 문자열
     * @return Map key:한글 키보드값, value: 해당하는 영어 alphabet
     */
    private Map<String,String> kor2EngMapping(String keyMap){
        Map<String,String> result = new HashMap<>();
        for(String key : keyMap.split("/")){
            String[] strs = key.split(",");
            result.put(strs[0],strs[1]);
        }
        return result;
    }
    /**
     * 겹 자음/모음 - 단일 자음/모음 매핑 문자열을 Map 형태로 변환
     * @param keyMap 사전에 정의한 겹 자음/모음에 해당하는 단일 자음/모음 문자열
     * @return Map key:겹 자음/모음 값, value: 해당하는 단일 자음/모음 값
     */
    private Map<String, String> doubleLetter(String keyMap){
        Map<String,String> result = new HashMap<>();
        for(String dict : keyMap.split("/")){
            String[] strs = dict.split(",");
            result.put(strs[0],strs[1]);
        }
        return  result;
    }
}