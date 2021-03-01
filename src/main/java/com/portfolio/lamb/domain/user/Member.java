package com.portfolio.lamb.domain.user;

import com.portfolio.lamb.domain.Timestamped;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Size(max = 30)
    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    private String email;

    private String picture;

    private boolean enabled = true;

//    private Map<String, String> socialIds = new HashMap<>();

    @Builder
    public Member(String username, String email, String picture, Role role) {
        this.username = username;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    public Member(String username, String password, Set<GrantedAuthority> grantedAuthorities) {
        super();
    }
}