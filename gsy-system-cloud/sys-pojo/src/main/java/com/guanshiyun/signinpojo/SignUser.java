package com.guanshiyun.signinpojo;

import com.guanshiyun.userpojo.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUser implements UserDetails {
    private  String username;
    private  String password;
    private  Collection<? extends GrantedAuthority> authorities;
    private SysUser sysUser;
    //用户昵称
    private String nickName;
    //验证码
    private String code;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
