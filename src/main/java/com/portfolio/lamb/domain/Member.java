package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.content.Timestamped;
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
import java.util.List;
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

    // @OneToMany(mappedBy = "member")
    // private List<Board> boardList;

    // @OneToMany(mappedBy = "member")
    // private List<Post> postList;

    public Member(MemberDto memberDto) {
        this.role = Role.USER;
        this.username = memberDto.getUsername();
        this.nickname = memberDto.getNickname();
        this.password = memberDto.getPassword();
        this.email = memberDto.getEmail();
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

    public void updateNicknameOrEmail(MemberDto memberDto) {
        this.nickname = !memberDto.getNickname().isEmpty() ? memberDto.getNickname() : this.nickname;
        this.email = !memberDto.getEmail().isEmpty() ? memberDto.getEmail() : this.email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}