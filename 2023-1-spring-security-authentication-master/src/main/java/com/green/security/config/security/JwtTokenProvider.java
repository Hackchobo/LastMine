package com.green.security.config.security;

import com.green.security.config.security.model.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component // 빈등록임 , 스프링이 객체주소값을 넣어준다.
public class JwtTokenProvider { // 싱글톤이란 여러개를 만들수있는것을 하나만 사용할수있게 하는것
   // private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    public final Key ACCESS_KEY;
    public final Key REFRESH_KEY;
    public final String TOKEN_TYPE; //

    public final long ACCESS_TOKEN_VALID_MS = 3_600_000L; // 1000L * 60 * 60 -> 1시간 , static 이있으면 객체화를 시켜야됨
    //public final long ACCESS_TOKEN_VALID_MS = 120_000L; // 1분
    public final long REFRESH_TOKEN_VALID_MS = 1_296_000_000L; // 1000L * 60 * 60 * 24 * 15 -> 15일

    public JwtTokenProvider(@Value("${springboot.jwt.access-secret}") String accessSecretKey
                            , @Value("${springboot.jwt.refresh-secret}") String refreshSecretKey
                            , @Value("${springboot.jwt.token-type}") String tokenType) {
        byte[] accessKeyBytes = Decoders.BASE64.decode(accessSecretKey);
        this.ACCESS_KEY = Keys.hmacShaKeyFor(accessKeyBytes); // 저장해야할 것을 암호화

        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecretKey);
        this.REFRESH_KEY = Keys.hmacShaKeyFor(refreshKeyBytes); // 저장해야할 것을 복호화 하는것
        this.TOKEN_TYPE = tokenType;
    }


    public String generateJwtToken(String strIuser, List<String> roles, long token_valid_ms, Key key) {
        log.info("JwtTokenProvider - generateJwtToken: 토큰 생성 시작");
        Date now = new Date(); // 현재 일자 시간 내용 다들어가게됨

        String token = Jwts.builder() // Static 메소드이다.
                .setClaims(createClaims(strIuser, roles)) // 담을 정보임, 갯수제한은 없지만 용량 제한이 있음.
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + token_valid_ms))// 위에날짜부터 해당 날짜 까지
                .signWith(key)
                .compact();
        log.info("JwtTokenProvider - generateJwtToken: 토큰 생성 완료");
        return token;
    }



    private Claims createClaims(String strIuser, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(strIuser); // .setSubject부분은 없어도된다.
        claims.put("roles", roles); // put이나오면 map이다
        //claims.put("name", "홍길동"); // (String, Object)
        return claims; // this는 멤버필드임, 상수임

        //return (Claims)Jwts.claims().setSubject
    }

    public Authentication getAuthentication (String token) {
        log.info("JwtTokenProvider - getAuthentication: 토큰 인증 정보 조회 시작");
        //UserDetails userDetails = SERVICE.loadUserByUsername(getUsername(token));
        UserDetails userDetails = getUserDetailsFromToken(token, ACCESS_KEY);
        log.info("JwtTokenProvider - getAuthentication: 토큰 인증 정보 조회 완료, UserDetails UserName : {}"
                , userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private UserDetails getUserDetailsFromToken(String token, Key key) {
        Claims claims = getClaims(token, key);
        String strIuser = claims.getSubject();
        List<String> roles = (List<String>)claims.get("roles");// get할때는 형변환이 되어야 한다.
        return MyUserDetails
                .builder()
                .iuser(Long.valueOf(strIuser))
                .roles(roles)
                .build();
    }

    public String resolveToken(HttpServletRequest req, String type) { // 요청에대한 정보
        log.info("JwtTokenProvider - resolveToken: HTTP 헤더에서 Token 값 추출");
        String headerAuth = req.getHeader("Authorization"); // 엑세스 토큰을 보낸다.
        return headerAuth == null ? null : headerAuth.substring(type.length()).trim();//substring 인자가 한개일 경우 시작인덱스 값전까지 자르기, 두개일 경우 첫번째 인텍스값부터 두번째 값까지
    }

    public Claims getClaims(String token, Key key) { // 고쳐
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isValidateToken(String token, Key key) {
        log.info("JwtTokenProvider - isValidateToken: 토큰 유효 체크 시작");
        try {
            return !getClaims(token, key).getExpiration().before(new Date()); // 현재 시간보다 만료 전 >true
        } catch (Exception e) {
            log.info("JwtTokenProvider - isValidateToken: 토큰 유효 체크 예외 발생");
            return false;
        }

        //지났으면 true > false;
        //안지났으면 false > true;
    }
}
