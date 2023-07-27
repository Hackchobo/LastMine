package com.green.security.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//spring security 5.7.0부터 WebSecurityConfigurerAdapter deprecated 됨
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration { //제일중요하다!!!! 클래스이름은 아무거나 상관없음

    private final JwtTokenProvider jwtTokenProvider;// final 한테는 @Autowired를 줄수없다.

    //webSecurityCustomizer를 제외한 모든 것, 시큐리티를 거친다. 보안과 연관
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception { // 요청들어올때마다 확인하는 구문
        httpSecurity.authorizeHttpRequests(authz ->
                    authz.requestMatchers(  // 가변 인자로 받는다.
                                    "/sign-api/sign-in"
                                    , "/sign-api/sign-up"
                                    , "/sign-api/exception"

                                    , "/swagger.html"
                                    , "/swagger-ui/**"
                                    , "/v3/api-docs/**"
                                    , "/static/imgs/**"
                                    , "/static/js/**"
                                    , "/static/css/**"
                                    , "/index.html"
                                    , "/"
                                    , "/view/**"
                            ).permitAll() // 이걸로 하는것은 모두다 허용을 한다. , 순서가 굉장히 중요 하다.
                            .requestMatchers(HttpMethod.GET, "/sign-api/refresh-token").permitAll() // 늘어난 부분임 누구나 접근 가능(get방식만 허용 하겠다.), 비로그인과 관련이 있다.
                            .requestMatchers(HttpMethod.GET, "/product/**").permitAll() // **(2차든 3차든 뭐든) *(2차까지만)
                            .requestMatchers("**exception**").permitAll()
                            .requestMatchers("/todo-api").hasAnyRole("USER", "ADMIN") // 정확하게 '/todo-api' 주소로 들어오면 CRUD 상관없이 권한이 필요하다.,HttpMethod.GET 이없으면 어느것이던 상관없음
                            .anyRequest().hasRole("ADMIN")// 어떠한 요청이라도 ADMIN 이여야한다. , 여러개를 받고싶다면 hasAnyRole, 한개는 hasRole
                ) //사용 권한 체크
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 사용 X, 세션처리를 하는데 끄면 절대 안됨
        .httpBasic(http -> http.disable()) //UI 있는 시큐리티 설정을 비활성화, 화면을 이용하려면 켜줘야하지만 백엔드는 UI를 사용을 안함
                .csrf(csrf -> csrf.disable()) //CSRF 보안이 필요 X, 쿠키와 세션을 이용해서 인증을 하고 있기 때문에 발생하는 일, https://kchanguk.tistory.com/197
                .exceptionHandling(except -> {
                    except.accessDeniedHandler(new CustomAccessDeniedHandler()); // 인가부분
                    except.authenticationEntryPoint(new CustomAuthenticationEntryPoint()); // 인증 부분
                })
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    //시큐리티를 거치지 않는다. 보안과 전혀 상관없는 페이지 및 리소스

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        //함수형 인터페이스 람다
//        WebSecurityCustomizer lamda = (web) -> web.ignoring()
//                    .requestMatchers(HttpMethod.GET, "/sign-api/refresh-token");
//        return lamda;
//    }
}
