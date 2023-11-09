package dev.globalgoals.security;

import dev.globalgoals.domain.Authority;
import dev.globalgoals.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class UserDetailsConfig implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        Set<Authority> authoritySet = user.getAuthorities();
        for (Authority authority : authoritySet) {
            authorities.add(new SimpleGrantedAuthority(authority.getAuthorityName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정의 만료 여부
        // true : 만료가 안됨
        // false: 만료 됨, 로그인 안됨
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정의 잠김 여부
        // true : 잠기지 않음
        // false: 잠김
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // password 만료 여부
        // true : 만료 안됨
        // false: 만료 됨, 로그인 안됨
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정 사용 여부
        // true = 1: 계정 활성화
        // false = 0: 계정 비활성화, 로그인 안됨
        return true;
    }
}
