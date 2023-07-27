package com.green.security.config.security.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class MyUserDetails implements UserDetails { // 로그인을 할때 무조건 있어야 하는 부분
    private Long iuser;
    private String uid;
    private String upw;
    private String name;

    @Builder.Default
    private List<String> roles = new ArrayList<>(); // 권한을 담는 부분


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 권한을 리턴하는 부분
        //return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()); // 밑에 구문이랑 비슷함
        return  this.roles.stream().map(item -> new SimpleGrantedAuthority(item)).collect(Collectors.toList()); // 타입 생략 가능, ; 생략 가능 , () 생략 가능(1개의 값만 받을때)
        // map - 같은크기의 배열을만든다음 그안에 값을 바꾼다.
        // 스트림은 일회성, 중간연산이 있고 최종연산이 꼭있어야 중산 연산을 한다.
        // 람다식 획일화된 처리가 가능하다.
        // 세션은 메모리에 저장이 된다. 서버에 저장되므로 서버 껏다키면 날라감.
    }

    @Override
    public String getPassword() { return this.upw; } // 비번

    @Override
    public String getUsername() { return this.uid; } // 아이디

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
