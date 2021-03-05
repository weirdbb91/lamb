package com.portfolio.lamb.domain.user;

import com.portfolio.lamb.domain.Timestamped;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Size(max = 30, message = "username length limit is 30")
    @Column(nullable = false, unique = true)
    private String username;

    @Size(max = 30, message = "nickname length limit is 30")
    @Column(unique = true)
    private String nickname;

    private String password;

    private String email;

    private boolean enabled = true;

    private boolean social = false;

    @Builder
    public Member(Role role, String username, String nickname, String password, String email) {
        this.role = role;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
    }

    public Member socialMember(String username) {
        this.role = Role.USER;
        this.username = username;
        this.nickname = username;
        this.password = username;
        this.social = true;
        return this;
    }

    public UserDetails getUserDetails() {
        return new User(username, password, getAuthorities());
    }

    public Set<GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.getValue()));
    }

    public void updateNicknameOrEmail(Member member) {
        this.nickname = !member.getNickname().isEmpty() ? member.getNickname() : this.nickname;
        this.email = !member.getEmail().isEmpty() ? member.getEmail() : this.email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}