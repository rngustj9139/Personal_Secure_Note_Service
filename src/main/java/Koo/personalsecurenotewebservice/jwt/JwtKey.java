package Koo.personalsecurenotewebservice.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.data.util.Pair;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Map;
import java.util.Random;

public class JwtKey { // jwt secret key를 제공하고 key rolling을 지원

    /**
     * Kid-Key List 외부로 절대 유출되어서는 안됩니다.
     **/
    private static final Map<String, String> SECRET_KEY_SET = Map.of( // kid와 secret key pair
            "key1", "SpringSecurityJWTPracticeProjectIsSoGoodAndThisProjectIsSoFunSpringSecurityJWTPracticeProjectIsSoGoodAndThisProjectIsSoFun",
            "key2", "GoodSpringSecurityNiceSpringSecurityGoodSpringSecurityNiceSpringSecurityGoodSpringSecurityNiceSpringSecurityGoodSpringSecurityNiceSpringSecurity",
            "key3", "HelloSpringSecurityHelloSpringSecurityHelloSpringSecurityHelloSpringSecurityHelloSpringSecurityHelloSpringSecurityHelloSpringSecurityHelloSpringSecurity"
    );

    /** [code description]
     *  for (String key : map.keySet()) {
     * 	String value = map.get(key);
     *     System.out.println("[key]:" + key + ", [value]:" + value);
     *  }
     *
     *  keySet()은 리스트를 반환한다. (map의 키값들이 들어있는 리스트가 반환된다.)
     *  List 컨테이너(ex - ArrayList<>())의 인스턴스를 배열(array)로 만드는것이 'toArray' 메서드이다.
     *  String 배열 인스턴스가 파라메터로 넘어갔는데, size를 '0'으로 명시했다.
     *  이는 List의 크기가 3이므로 원래 List의 size로 배열이 만들어진 것을 의미한다.
     **/
    private static final String[] KID_SET = SECRET_KEY_SET.keySet().toArray(new String[0]);
    private static Random randomIndex = new Random();

    /**
     * 3가지의 SECRET_KEY_SET에서 랜덤한 KEY 가져오기
     *
     * @return kid와 key Pair
     **/
    public static Pair<String, Key> getRandomKey() {
        String kid = KID_SET[randomIndex.nextInt(KID_SET.length)];
        String secretKey = SECRET_KEY_SET.get(kid);
        return Pair.of(kid, Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * kid를 받고 그 kid로 secret Key찾기
     *
     * @param kid kid
     * @return Key
     **/
    public static Key getKey(String kid) {
        String key = SECRET_KEY_SET.getOrDefault(kid, null);
        if (key == null)
            return null;
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)); // key의 길이에 따라서 적절한 암호화 방식을 선택해준다.
    }

}
